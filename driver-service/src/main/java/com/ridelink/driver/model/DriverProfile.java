package com.ridelink.driver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "driver_profiles")
public class DriverProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String fullName;
    private String licenseNumber;
    private int experienceYears;
    private Vehicle vehicle;
    private DriverStatus status = DriverStatus.OFFLINE;
    private GeoLocation currentLocation;
    private String serviceArea;
    private double rating = 5.0;
    private String activeRideId;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public DriverProfile() {
    }

    public DriverProfile(String userId, String fullName, String licenseNumber, int experienceYears, Vehicle vehicle, String serviceArea) {
        this.userId = userId;
        this.fullName = fullName;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
        this.vehicle = vehicle;
        this.serviceArea = serviceArea;
        this.status = DriverStatus.OFFLINE;
        this.rating = 5.0;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
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
        this.updatedAt = Instant.now();
    }

    public GeoLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoLocation currentLocation) {
        this.currentLocation = currentLocation;
        this.updatedAt = Instant.now();
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
        this.updatedAt = Instant.now();
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
