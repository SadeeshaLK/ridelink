package com.ridelink.fare.dto;

import com.ridelink.fare.model.FareCalculationBreakdown;
import com.ridelink.fare.model.VehicleType;

public class FareEstimateResponse {

    private VehicleType vehicleType;
    private double estimatedDistanceKm;
    private double estimatedDurationMinutes;
    private double estimatedFare;
    private String currency;
    private String calculationRule;
    private FareCalculationBreakdown breakdown;

    public FareEstimateResponse() {
    }

    public FareEstimateResponse(VehicleType vehicleType, double estimatedDistanceKm, double estimatedDurationMinutes, double estimatedFare, String currency, String calculationRule, FareCalculationBreakdown breakdown) {
        this.vehicleType = vehicleType;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.estimatedFare = estimatedFare;
        this.currency = currency;
        this.calculationRule = calculationRule;
        this.breakdown = breakdown;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getEstimatedDistanceKm() {
        return estimatedDistanceKm;
    }

    public void setEstimatedDistanceKm(double estimatedDistanceKm) {
        this.estimatedDistanceKm = estimatedDistanceKm;
    }

    public double getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(double estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCalculationRule() {
        return calculationRule;
    }

    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }

    public FareCalculationBreakdown getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(FareCalculationBreakdown breakdown) {
        this.breakdown = breakdown;
    }
}
