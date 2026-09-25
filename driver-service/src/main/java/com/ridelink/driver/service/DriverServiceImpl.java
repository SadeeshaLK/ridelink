package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DriverAlreadyExistsException;
import com.ridelink.driver.exception.ResourceNotFoundException;
import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.model.DriverStatus;
import com.ridelink.driver.model.GeoLocation;
import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final DriverProfileRepository driverRepository;

    public DriverServiceImpl(DriverProfileRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public DriverResponse createProfile(CreateDriverProfileRequest request) {
        if (driverRepository.existsByUserId(request.getUserId())) {
            throw new DriverAlreadyExistsException("Driver profile already exists for user ID: " + request.getUserId());
        }

        VehicleDto v = request.getVehicle();
        Vehicle vehicle = new Vehicle(
                v.getMake(),
                v.getModel(),
                v.getYear(),
                v.getLicensePlate(),
                v.getVehicleType(),
                v.getCapacity()
        );

        DriverProfile profile = new DriverProfile(
                request.getUserId(),
                request.getFullName(),
                request.getLicenseNumber(),
                request.getExperienceYears(),
                vehicle,
                request.getServiceArea()
        );

        // Default initial simulated location centered around service area
        profile.setCurrentLocation(new GeoLocation(6.9271, 79.8612, request.getServiceArea())); // Default Colombo coords

        DriverProfile saved = driverRepository.save(profile);
        return DriverResponse.fromEntity(saved);
    }

    @Override
    public DriverResponse getProfileById(String id) {
        DriverProfile profile = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return DriverResponse.fromEntity(profile);
    }

    @Override
    public DriverResponse getProfileByUserId(String userId) {
        DriverProfile profile = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found for user id: " + userId));
        return DriverResponse.fromEntity(profile);
    }

    @Override
    public DriverResponse updateLocation(String id, UpdateLocationRequest request) {
        DriverProfile profile = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        profile.setCurrentLocation(new GeoLocation(request.getLatitude(), request.getLongitude(), request.getAddress()));
        DriverProfile updated = driverRepository.save(profile);
        return DriverResponse.fromEntity(updated);
    }

    @Override
    public DriverResponse updateAvailability(String id, UpdateAvailabilityRequest request) {
        DriverProfile profile = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        profile.setStatus(request.getStatus());
        if (request.getActiveRideId() != null) {
            profile.setActiveRideId(request.getActiveRideId());
        } else if (request.getStatus() == DriverStatus.AVAILABLE || request.getStatus() == DriverStatus.OFFLINE) {
            profile.setActiveRideId(null);
        }

        DriverProfile updated = driverRepository.save(profile);
        return DriverResponse.fromEntity(updated);
    }

    @Override
    public List<EligibleDriverResponse> findEligibleDrivers(EligibleDriverSearchRequest request) {
        // Query drivers with status AVAILABLE and optionally filter by vehicle type
        List<DriverProfile> availableDrivers;
        if (request.getVehicleType() != null) {
            availableDrivers = driverRepository.findByStatusAndVehicleVehicleType(DriverStatus.AVAILABLE, request.getVehicleType());
        } else {
            availableDrivers = driverRepository.findByStatus(DriverStatus.AVAILABLE);
        }

        List<EligibleDriverResponse> results = new ArrayList<>();
        double maxRadius = request.getMaxRadiusKm() > 0 ? request.getMaxRadiusKm() : 25.0;

        for (DriverProfile driver : availableDrivers) {
            double distance = 0.0;
            if (driver.getCurrentLocation() != null) {
                distance = calculateDistanceKm(
                        request.getPickupLatitude(),
                        request.getPickupLongitude(),
                        driver.getCurrentLocation().getLatitude(),
                        driver.getCurrentLocation().getLongitude()
                );
            }

            // Include drivers within search radius
            if (distance <= maxRadius) {
                results.add(new EligibleDriverResponse(
                        driver.getId(),
                        driver.getUserId(),
                        driver.getFullName(),
                        driver.getVehicle(),
                        driver.getStatus(),
                        driver.getCurrentLocation(),
                        driver.getRating(),
                        Math.round(distance * 100.0) / 100.0 // 2 decimal places
                ));
            }
        }

        // Sort by distance (closest driver first)
        results.sort(Comparator.comparingDouble(EligibleDriverResponse::getDistanceKm));
        return results;
    }

    @Override
    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(DriverResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverResponse> getDriversByStatus(DriverStatus status) {
        return driverRepository.findByStatus(status).stream()
                .map(DriverResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Haversine formula to compute great-circle distance between two GPS coordinates in kilometers.
     */
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
