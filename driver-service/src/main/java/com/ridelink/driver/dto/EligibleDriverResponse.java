package com.ridelink.driver.dto;

import com.ridelink.driver.model.DriverStatus;
import com.ridelink.driver.model.GeoLocation;
import com.ridelink.driver.model.Vehicle;

public class EligibleDriverResponse {

    private String driverId;
    private String userId;
    private String fullName;
    private Vehicle vehicle;
    private DriverStatus status;
    private GeoLocation currentLocation;
    private double rating;
    private double distanceKm;

    public EligibleDriverResponse() {
    }

    public EligibleDriverResponse(String driverId, String userId, String fullName, Vehicle vehicle, DriverStatus status, GeoLocation currentLocation, double rating, double distanceKm) {
        this.driverId = driverId;
        this.userId = userId;
        this.fullName = fullName;
        this.vehicle = vehicle;
        this.status = status;
        this.currentLocation = currentLocation;
        this.rating = rating;
        this.distanceKm = distanceKm;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public GeoLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }
}
