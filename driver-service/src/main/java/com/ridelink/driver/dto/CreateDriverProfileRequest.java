package com.ridelink.driver.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateDriverProfileRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @Min(value = 0, message = "Experience years cannot be negative")
    private int experienceYears;

    @Valid
    @NotNull(message = "Vehicle details are required")
    private VehicleDto vehicle;

    @NotBlank(message = "Service area is required")
    private String serviceArea;

    public CreateDriverProfileRequest() {
    }

    public CreateDriverProfileRequest(String userId, String fullName, String licenseNumber, int experienceYears, VehicleDto vehicle, String serviceArea) {
        this.userId = userId;
        this.fullName = fullName;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
        this.vehicle = vehicle;
        this.serviceArea = serviceArea;
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

    public VehicleDto getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDto vehicle) {
        this.vehicle = vehicle;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }
}
