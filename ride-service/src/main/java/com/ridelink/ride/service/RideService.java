package com.ridelink.ride.service;

import com.ridelink.ride.dto.CancelRideRequest;
import com.ridelink.ride.dto.CompleteRideRequest;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.model.RideStatus;

import java.util.List;

public interface RideService {
    RideResponse requestRide(CreateRideRequest request);
    RideResponse assignDriver(String rideId, String driverId);
    RideResponse acceptRide(String rideId, String driverId);
    RideResponse startRide(String rideId, String driverId);
    RideResponse completeRide(String rideId, CompleteRideRequest request);
    RideResponse cancelRide(String rideId, CancelRideRequest request);
    RideResponse getRideById(String rideId);
    List<RideResponse> getRidesByPassengerId(String passengerId);
    List<RideResponse> getRidesByDriverId(String driverId);
    List<RideResponse> getRidesByStatus(RideStatus status);
    List<RideResponse> getAllRides();
}
