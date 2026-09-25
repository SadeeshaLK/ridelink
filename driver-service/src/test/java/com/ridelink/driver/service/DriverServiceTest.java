package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DriverAlreadyExistsException;
import com.ridelink.driver.exception.ResourceNotFoundException;
import com.ridelink.driver.model.*;
import com.ridelink.driver.repository.DriverProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverProfileRepository driverRepository;

    @InjectMocks
    private DriverServiceImpl driverService;

    private DriverProfile sampleDriver;

    @BeforeEach
    void setUp() {
        Vehicle vehicle = new Vehicle("Toyota", "Prius", 2021, "WP-CAD-1234", VehicleType.SEDAN, 4);
        sampleDriver = new DriverProfile("user-driver-1", "Kamal Perera", "DL-998877", 5, vehicle, "Colombo");
        sampleDriver.setId("driver-101");
        sampleDriver.setStatus(DriverStatus.AVAILABLE);
        sampleDriver.setCurrentLocation(new GeoLocation(6.9271, 79.8612, "Colombo Fort"));
    }

    @Test
    @DisplayName("Should successfully create a driver profile")
    void testCreateProfileSuccess() {
        VehicleDto v = new VehicleDto("Toyota", "Prius", 2021, "WP-CAD-1234", VehicleType.SEDAN, 4);
        CreateDriverProfileRequest req = new CreateDriverProfileRequest("user-driver-1", "Kamal Perera", "DL-998877", 5, v, "Colombo");

        when(driverRepository.existsByUserId("user-driver-1")).thenReturn(false);
        when(driverRepository.save(any(DriverProfile.class))).thenReturn(sampleDriver);

        DriverResponse response = driverService.createProfile(req);

        assertThat(response).isNotNull();
        assertThat(response.getFullName()).isEqualTo("Kamal Perera");
        assertThat(response.getVehicle().getLicensePlate()).isEqualTo("WP-CAD-1234");
        verify(driverRepository, times(1)).save(any(DriverProfile.class));
    }

    @Test
    @DisplayName("Should throw exception when creating profile for already registered driver user")
    void testCreateProfileAlreadyExists() {
        VehicleDto v = new VehicleDto("Toyota", "Prius", 2021, "WP-CAD-1234", VehicleType.SEDAN, 4);
        CreateDriverProfileRequest req = new CreateDriverProfileRequest("user-driver-1", "Kamal Perera", "DL-998877", 5, v, "Colombo");

        when(driverRepository.existsByUserId("user-driver-1")).thenReturn(true);

        assertThatThrownBy(() -> driverService.createProfile(req))
                .isInstanceOf(DriverAlreadyExistsException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should update driver availability status to BUSY")
    void testUpdateAvailability() {
        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));
        when(driverRepository.save(any(DriverProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateAvailabilityRequest req = new UpdateAvailabilityRequest(DriverStatus.BUSY, "ride-999");
        DriverResponse response = driverService.updateAvailability("driver-101", req);

        assertThat(response.getStatus()).isEqualTo(DriverStatus.BUSY);
        assertThat(response.getActiveRideId()).isEqualTo("ride-999");
        verify(driverRepository).save(sampleDriver);
    }

    @Test
    @DisplayName("Should find eligible available drivers within radius and sort by proximity")
    void testFindEligibleDrivers() {
        // Driver at 6.9271, 79.8612 (Colombo Fort)
        when(driverRepository.findByStatusAndVehicleVehicleType(DriverStatus.AVAILABLE, VehicleType.SEDAN))
                .thenReturn(List.of(sampleDriver));

        // Passenger requesting pickup near Galle Face (6.9200, 79.8450)
        EligibleDriverSearchRequest searchReq = new EligibleDriverSearchRequest(6.9200, 79.8450, VehicleType.SEDAN, 10.0);

        List<EligibleDriverResponse> eligible = driverService.findEligibleDrivers(searchReq);

        assertThat(eligible).hasSize(1);
        assertThat(eligible.get(0).getDriverId()).isEqualTo("driver-101");
        assertThat(eligible.get(0).getDistanceKm()).isLessThan(5.0); // Close distance
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when driver profile not found")
    void testGetProfileNotFound() {
        when(driverRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getProfileById("invalid-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Driver not found");
    }
}
