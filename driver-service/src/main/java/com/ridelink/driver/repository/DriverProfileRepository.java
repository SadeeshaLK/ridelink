package com.ridelink.driver.repository;

import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.model.DriverStatus;
import com.ridelink.driver.model.VehicleType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverProfileRepository extends MongoRepository<DriverProfile, String> {
    Optional<DriverProfile> findByUserId(String userId);
    boolean existsByUserId(String userId);
    List<DriverProfile> findByStatus(DriverStatus status);
    List<DriverProfile> findByStatusAndVehicleVehicleType(DriverStatus status, VehicleType vehicleType);
}
