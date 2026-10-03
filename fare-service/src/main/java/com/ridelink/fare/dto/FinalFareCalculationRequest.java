package com.ridelink.fare.dto;

import com.ridelink.fare.model.VehicleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FinalFareCalculationRequest {

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @DecimalMin(value = "0.01", message = "Actual distance must be positive")
    private double actualDistanceKm;

    @DecimalMin(value = "0.1", message = "Actual duration must be positive")
    private double actualDurationMinutes;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private double surgeMultiplier = 1.0;
    private double waitingTimeMinutes = 0.0;
    private double discountAmount = 0.0;

    public FinalFareCalculationRequest() {
    }

    public FinalFareCalculationRequest(String rideId, double actualDistanceKm, double actualDurationMinutes, VehicleType vehicleType) {
        this.rideId = rideId;
        this.actualDistanceKm = actualDistanceKm;
        this.actualDurationMinutes = actualDurationMinutes;
        this.vehicleType = vehicleType;
        this.surgeMultiplier = 1.0;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public double getActualDistanceKm() {
        return actualDistanceKm;
    }

    public void setActualDistanceKm(double actualDistanceKm) {
        this.actualDistanceKm = actualDistanceKm;
    }

    public double getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(double actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public void setSurgeMultiplier(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    public double getWaitingTimeMinutes() {
        return waitingTimeMinutes;
    }

    public void setWaitingTimeMinutes(double waitingTimeMinutes) {
        this.waitingTimeMinutes = waitingTimeMinutes;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
}
