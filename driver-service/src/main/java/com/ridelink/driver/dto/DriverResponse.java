package com.ridelink.driver.dto;

import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.model.DriverStatus;
import com.ridelink.driver.model.GeoLocation;
import com.ridelink.driver.model.Vehicle;

import java.time.Instant;

public class DriverResponse {

    private String id;
    private String userId;
    private String fullName;
    private String licenseNumber;
    private int experienceYears;
    private Vehicle vehicle;
    private DriverStatus status;
    private GeoLocation currentLocation;
    private String serviceArea;
    private double rating;
    private String activeRideId;
    private Instant createdAt;
    private Instant updatedAt;

    public DriverResponse() {
    }

    public static DriverResponse fromEntity(DriverProfile profile) {
        DriverResponse resp = new DriverResponse();
        resp.setId(profile.getId());
        resp.setUserId(profile.getUserId());
        resp.setFullName(profile.getFullName());
        resp.setLicenseNumber(profile.getLicenseNumber());
        resp.setExperienceYears(profile.getExperienceYears());
        resp.setVehicle(profile.getVehicle());
        resp.setStatus(profile.getStatus());
        resp.setCurrentLocation(profile.getCurrentLocation());
        resp.setServiceArea(profile.getServiceArea());
        resp.setRating(profile.getRating());
        resp.setActiveRideId(profile.getActiveRideId());
        resp.setCreatedAt(profile.getCreatedAt());
        resp.setUpdatedAt(profile.getUpdatedAt());
        return resp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
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

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getActiveRideId() {
        return activeRideId;
    }

    public void setActiveRideId(String activeRideId) {
        this.activeRideId = activeRideId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
