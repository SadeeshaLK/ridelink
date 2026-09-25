package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.model.DriverStatus;

import java.util.List;

public interface DriverService {
    DriverResponse createProfile(CreateDriverProfileRequest request);
    DriverResponse getProfileById(String id);
    DriverResponse getProfileByUserId(String userId);
    DriverResponse updateLocation(String id, UpdateLocationRequest request);
    DriverResponse updateAvailability(String id, UpdateAvailabilityRequest request);
    List<EligibleDriverResponse> findEligibleDrivers(EligibleDriverSearchRequest request);
    List<DriverResponse> getAllDrivers();
    List<DriverResponse> getDriversByStatus(DriverStatus status);
}
