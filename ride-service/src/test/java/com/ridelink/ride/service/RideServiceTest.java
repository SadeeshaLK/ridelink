package com.ridelink.ride.service;

import com.ridelink.ride.client.AccountServiceClient;
import com.ridelink.ride.client.DriverServiceClient;
import com.ridelink.ride.client.FareServiceClient;
import com.ridelink.ride.dto.*;
import com.ridelink.ride.exception.InvalidRideStatusTransitionException;
import com.ridelink.ride.exception.NoAvailableDriverException;
import com.ridelink.ride.model.Location;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.model.VehicleType;
import com.ridelink.ride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private FareServiceClient fareServiceClient;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private RideServiceImpl rideService;

    private Ride sampleRide;

    @BeforeEach
    void setUp() {
        sampleRide = new Ride(
                "passenger-101",
                VehicleType.SEDAN,
                new Location(6.9271, 79.8612, "Fort Colombo"),
                new Location(6.9010, 79.8540, "Kollupitiya"),
                3.5,
                450.0
        );
        sampleRide.setId("ride-001");
    }

    @Test
    @DisplayName("Should successfully request ride with fare estimation")
    void testRequestRideSuccess() {
        CreateRideRequest request = new CreateRideRequest(
                "passenger-101",
                VehicleType.SEDAN,
                new LocationDto(6.9271, 79.8612, "Fort Colombo"),
                new LocationDto(6.9010, 79.8540, "Kollupitiya")
        );

        FareEstimateClientResponse fareResp = new FareEstimateClientResponse();
        fareResp.setEstimatedFare(450.0);
        fareResp.setEstimatedDistanceKm(3.5);

        when(accountServiceClient.isPassengerValid("passenger-101")).thenReturn(true);
        when(fareServiceClient.estimateFare(anyDouble(), eq(VehicleType.SEDAN))).thenReturn(fareResp);
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.requestRide(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(RideStatus.REQUESTED);
        assertThat(response.getEstimatedFare()).isEqualTo(450.0);
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    @DisplayName("Should assign closest eligible driver and transition to ASSIGNED")
    void testAssignDriverSuccess() {
        when(rideRepository.findById("ride-001")).thenReturn(Optional.of(sampleRide));

        EligibleDriverClientResponse eligibleDriver = new EligibleDriverClientResponse();
        eligibleDriver.setDriverId("driver-202");
        eligibleDriver.setDistanceKm(1.2);

        when(driverServiceClient.findEligibleDrivers(anyDouble(), anyDouble(), eq(VehicleType.SEDAN), anyDouble()))
                .thenReturn(List.of(eligibleDriver));
        when(rideRepository.save(any(Ride.class))).thenAnswer(i -> i.getArgument(0));

        RideResponse response = rideService.assignDriver("ride-001", null);

        assertThat(response.getStatus()).isEqualTo(RideStatus.ASSIGNED);
        assertThat(response.getDriverId()).isEqualTo("driver-202");
        verify(driverServiceClient).updateDriverAvailability("driver-202", "BUSY", "ride-001");
    }

    @Test
    @DisplayName("Should throw NoAvailableDriverException when no driver found (Negative scenario 1)")
    void testAssignDriverNoneAvailable() {
        when(rideRepository.findById("ride-001")).thenReturn(Optional.of(sampleRide));
        when(driverServiceClient.findEligibleDrivers(anyDouble(), anyDouble(), eq(VehicleType.SEDAN), anyDouble()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> rideService.assignDriver("ride-001", null))
                .isInstanceOf(NoAvailableDriverException.class)
                .hasMessageContaining("No available drivers found");
    }

    @Test
    @DisplayName("Should validate lifecycle transition: ASSIGNED -> ACCEPTED -> IN_PROGRESS")
    void testLifecycleTransitions() {
        sampleRide.setStatus(RideStatus.ASSIGNED);
        sampleRide.setDriverId("driver-202");
        when(rideRepository.findById("ride-001")).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenAnswer(i -> i.getArgument(0));

        // Accept
        RideResponse accepted = rideService.acceptRide("ride-001", "driver-202");
        assertThat(accepted.getStatus()).isEqualTo(RideStatus.ACCEPTED);

        // Start
        RideResponse started = rideService.startRide("ride-001", "driver-202");
        assertThat(started.getStatus()).isEqualTo(RideStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should throw InvalidRideStatusTransitionException on illegal state jump (Negative scenario 2)")
    void testInvalidStatusTransition() {
        sampleRide.setStatus(RideStatus.COMPLETED);
        when(rideRepository.findById("ride-001")).thenReturn(Optional.of(sampleRide));

        // Attempting to cancel an already completed ride
        CancelRideRequest cancelReq = new CancelRideRequest("Too late");
        assertThatThrownBy(() -> rideService.cancelRide("ride-001", cancelReq))
                .isInstanceOf(InvalidRideStatusTransitionException.class)
                .hasMessageContaining("already in terminal state");
    }
}
