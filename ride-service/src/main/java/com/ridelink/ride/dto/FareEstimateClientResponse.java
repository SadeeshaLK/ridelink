package com.ridelink.ride.dto;

public class FareEstimateClientResponse {

    private double estimatedFare;
    private double estimatedDistanceKm;
    private double estimatedDurationMinutes;
    private String currency;
    private String calculationRule;

    public FareEstimateClientResponse() {
    }

    public double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(double estimatedFare) {
        this.estimatedFare = estimatedFare;
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
}
