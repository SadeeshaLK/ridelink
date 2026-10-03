package com.ridelink.ride.dto;

import jakarta.validation.constraints.DecimalMin;

public class CompleteRideRequest {

    @DecimalMin(value = "0.01", message = "Actual distance must be positive")
    private double actualDistanceKm;

    @DecimalMin(value = "0.1", message = "Actual duration minutes must be positive")
    private double actualDurationMinutes;

    private String paymentMethod = "CARD";

    public CompleteRideRequest() {
    }

    public CompleteRideRequest(double actualDistanceKm, double actualDurationMinutes) {
        this.actualDistanceKm = actualDistanceKm;
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public CompleteRideRequest(double actualDistanceKm, double actualDurationMinutes, String paymentMethod) {
        this.actualDistanceKm = actualDistanceKm;
        this.actualDurationMinutes = actualDurationMinutes;
        this.paymentMethod = paymentMethod;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
