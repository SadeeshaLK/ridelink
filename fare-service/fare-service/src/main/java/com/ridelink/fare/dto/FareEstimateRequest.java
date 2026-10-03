package com.ridelink.fare.dto;

import com.ridelink.fare.model.VehicleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class FareEstimateRequest {

    private Double pickupLatitude;
    private Double pickupLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    @DecimalMin(value = "0.1", message = "Estimated distance must be greater than 0")
    private double estimatedDistanceKm;

    @NotNull(message = "Vehicle type is required (SEDAN, SUV, HATCHBACK, MOTORBIKE)")
    private VehicleType vehicleType;

    private double surgeMultiplier = 1.0;

    public FareEstimateRequest() {
    }

    public FareEstimateRequest(double estimatedDistanceKm, VehicleType vehicleType) {
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.vehicleType = vehicleType;
        this.surgeMultiplier = 1.0;
    }

    public FareEstimateRequest(Double pickupLatitude, Double pickupLongitude, Double destinationLatitude, Double destinationLongitude, double estimatedDistanceKm, VehicleType vehicleType, double surgeMultiplier) {
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.vehicleType = vehicleType;
        this.surgeMultiplier = surgeMultiplier > 0 ? surgeMultiplier : 1.0;
    }

    public Double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public Double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public double getEstimatedDistanceKm() {
        return estimatedDistanceKm;
    }

    public void setEstimatedDistanceKm(double estimatedDistanceKm) {
        this.estimatedDistanceKm = estimatedDistanceKm;
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
}
