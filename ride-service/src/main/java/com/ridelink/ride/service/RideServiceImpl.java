package com.ridelink.ride.service;

import com.ridelink.ride.client.AccountServiceClient;
import com.ridelink.ride.client.DriverServiceClient;
import com.ridelink.ride.client.FareServiceClient;
import com.ridelink.ride.dto.*;
import com.ridelink.ride.exception.InvalidRideStatusTransitionException;
import com.ridelink.ride.exception.NoAvailableDriverException;
import com.ridelink.ride.exception.ResourceNotFoundException;
import com.ridelink.ride.model.Location;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideServiceImpl implements RideService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final RideRepository rideRepository;
    private final DriverServiceClient driverServiceClient;
    private final FareServiceClient fareServiceClient;
    private final AccountServiceClient accountServiceClient;

    public RideServiceImpl(RideRepository rideRepository,
                           DriverServiceClient driverServiceClient,
                           FareServiceClient fareServiceClient,
                           AccountServiceClient accountServiceClient) {
        this.rideRepository = rideRepository;
        this.driverServiceClient = driverServiceClient;
        this.fareServiceClient = fareServiceClient;
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    public RideResponse requestRide(CreateRideRequest request) {
        // Interservice check: Validate passenger is registered and active
        boolean isPassengerActive = accountServiceClient.isPassengerValid(request.getPassengerId());
        if (!isPassengerActive) {
            throw new ResourceNotFoundException("Passenger is not registered or account is suspended: " + request.getPassengerId());
        }

        Location pickup = new Location(
                request.getPickupLocation().getLatitude(),
                request.getPickupLocation().getLongitude(),
                request.getPickupLocation().getAddress()
        );

        Location destination = new Location(
                request.getDestinationLocation().getLatitude(),
                request.getDestinationLocation().getLongitude(),
                request.getDestinationLocation().getAddress()
        );

        double distanceKm = calculateDistanceKm(pickup.getLatitude(), pickup.getLongitude(), destination.getLatitude(), destination.getLongitude());
        if (distanceKm < 0.5) {
            distanceKm = 2.5; // fallback simulated min distance if same spot
        }
        distanceKm = Math.round(distanceKm * 100.0) / 100.0;

        // Interservice call to Fare Service
        FareEstimateClientResponse fareEstimate = fareServiceClient.estimateFare(distanceKm, request.getVehicleType());

        Ride ride = new Ride(
                request.getPassengerId(),
                request.getVehicleType(),
                pickup,
                destination,
                distanceKm,
                fareEstimate.getEstimatedFare()
        );

        Ride saved = rideRepository.save(ride);
        return RideResponse.fromEntity(saved);
    }

    @Override
    public RideResponse assignDriver(String rideId, String specificDriverId) {
        Ride ride = getRideEntity(rideId);

        // State Machine validation: Can only assign when REQUESTED
        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStatusTransitionException(
                    "Cannot assign driver. Ride is in state: " + ride.getStatus() + ". Expected: REQUESTED");
        }

        String assignedDriverId = specificDriverId;

        if (assignedDriverId == null || assignedDriverId.isBlank()) {
            // Interservice call to Driver Service to search nearby eligible available drivers
            List<EligibleDriverClientResponse> eligibleDrivers = driverServiceClient.findEligibleDrivers(
                    ride.getPickupLocation().getLatitude(),
                    ride.getPickupLocation().getLongitude(),
                    ride.getVehicleType(),
                    25.0
            );

            // Negative scenario 1 demonstration: No available driver
            if (eligibleDrivers.isEmpty()) {
                throw new NoAvailableDriverException(
                        "No available drivers found in the pickup vicinity for vehicle type: " + ride.getVehicleType());
            }

            assignedDriverId = eligibleDrivers.get(0).getDriverId();
        }

        ride.setDriverId(assignedDriverId);
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setAssignedAt(Instant.now());

        // Interservice call: Notify Driver Service to mark driver BUSY
        driverServiceClient.updateDriverAvailability(assignedDriverId, "BUSY", ride.getId());

        Ride updated = rideRepository.save(ride);
        return RideResponse.fromEntity(updated);
    }

    @Override
    public RideResponse acceptRide(String rideId, String driverId) {
        Ride ride = getRideEntity(rideId);

        // State Machine validation: Can only accept when ASSIGNED
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new InvalidRideStatusTransitionException(
                    "Cannot accept ride. Ride is in state: " + ride.getStatus() + ". Expected: ASSIGNED");
        }

        if (driverId != null && !driverId.equals(ride.getDriverId())) {
            throw new InvalidRideStatusTransitionException("Driver " + driverId + " is not the assigned driver for this ride.");
        }

        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(Instant.now());

        Ride updated = rideRepository.save(ride);
        return RideResponse.fromEntity(updated);
    }

    @Override
    public RideResponse startRide(String rideId, String driverId) {
        Ride ride = getRideEntity(rideId);

        // State Machine validation: Can only start when ACCEPTED
        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new InvalidRideStatusTransitionException(
                    "Cannot start ride. Ride is in state: " + ride.getStatus() + ". Expected: ACCEPTED");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(Instant.now());

        Ride updated = rideRepository.save(ride);
        return RideResponse.fromEntity(updated);
    }

    @Override
    public RideResponse completeRide(String rideId, CompleteRideRequest request) {
        Ride ride = getRideEntity(rideId);

        // State Machine validation: Can only complete when IN_PROGRESS
        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new InvalidRideStatusTransitionException(
                    "Cannot complete ride. Ride is in state: " + ride.getStatus() + ". Expected: IN_PROGRESS");
        }

        double actualDistance = request.getActualDistanceKm() > 0 ? request.getActualDistanceKm() : ride.getEstimatedDistanceKm();
        double actualDuration = request.getActualDurationMinutes() > 0 ? request.getActualDurationMinutes() : 15.0;

        // Interservice call to Fare Service to calculate final fare
        FinalFareClientResponse finalFareResponse = fareServiceClient.calculateFinalFare(
                ride.getId(),
                actualDistance,
                actualDuration,
                ride.getVehicleType()
        );

        // Interservice call to Fare Service to record simulated payment
        String paymentId = fareServiceClient.processPayment(
                ride.getId(),
                ride.getPassengerId(),
                ride.getDriverId(),
                finalFareResponse.getFinalFare(),
                request.getPaymentMethod()
        );

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(Instant.now());
        ride.setFinalFare(finalFareResponse.getFinalFare());
        ride.setPaymentId(paymentId);

        // Interservice call to Driver Service: release driver back to AVAILABLE
        if (ride.getDriverId() != null) {
            driverServiceClient.updateDriverAvailability(ride.getDriverId(), "AVAILABLE", null);
        }

        Ride updated = rideRepository.save(ride);
        return RideResponse.fromEntity(updated);
    }

    @Override
    public RideResponse cancelRide(String rideId, CancelRideRequest request) {
        Ride ride = getRideEntity(rideId);

        // Negative scenario 2 demonstration: Terminal states cannot be cancelled
        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new InvalidRideStatusTransitionException(
                    "Cannot cancel ride. Ride is already in terminal state: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(Instant.now());
        ride.setCancellationReason(request.getReason());

        // Interservice call: release driver if assigned
        if (ride.getDriverId() != null) {
            driverServiceClient.updateDriverAvailability(ride.getDriverId(), "AVAILABLE", null);
        }

        Ride updated = rideRepository.save(ride);
        return RideResponse.fromEntity(updated);
    }

    @Override
    public RideResponse getRideById(String rideId) {
        return RideResponse.fromEntity(getRideEntity(rideId));
    }

    @Override
    public List<RideResponse> getRidesByPassengerId(String passengerId) {
        return rideRepository.findByPassengerId(passengerId).stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RideResponse> getRidesByDriverId(String driverId) {
        return rideRepository.findByDriverId(driverId).stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RideResponse> getRidesByStatus(RideStatus status) {
        return rideRepository.findByStatus(status).stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RideResponse> getAllRides() {
        return rideRepository.findAll().stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private Ride getRideEntity(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
