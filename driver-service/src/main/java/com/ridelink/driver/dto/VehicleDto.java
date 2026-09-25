package com.ridelink.driver.dto;

import com.ridelink.driver.model.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehicleDto {

    @NotBlank(message = "Vehicle make is required")
    private String make;

    @NotBlank(message = "Vehicle model is required")
    private String model;

    @Min(value = 2000, message = "Year must be 2000 or newer")
    private int year;

    @NotBlank(message = "License plate number is required")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required (SEDAN, SUV, HATCHBACK, MOTORBIKE)")
    private VehicleType vehicleType;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    public VehicleDto() {
    }

    public VehicleDto(String make, String model, int year, String licensePlate, VehicleType vehicleType, int capacity) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.capacity = capacity;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
