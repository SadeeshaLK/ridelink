package com.ridelink.fare.model;

public class FareCalculationBreakdown {

    private double baseFare;
    private double distanceKm;
    private double distanceCharge;
    private double durationMinutes;
    private double durationCharge;
    private double vehicleMultiplier;
    private double surgeMultiplier;
    private double totalFare;
    private String currency;

    public FareCalculationBreakdown() {
    }

    public FareCalculationBreakdown(double baseFare, double distanceKm, double distanceCharge, double durationMinutes, double durationCharge, double vehicleMultiplier, double surgeMultiplier, double totalFare, String currency) {
        this.baseFare = baseFare;
        this.distanceKm = distanceKm;
        this.distanceCharge = distanceCharge;
        this.durationMinutes = durationMinutes;
        this.durationCharge = durationCharge;
        this.vehicleMultiplier = vehicleMultiplier;
        this.surgeMultiplier = surgeMultiplier;
        this.totalFare = totalFare;
        this.currency = currency;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getDistanceCharge() {
        return distanceCharge;
    }

    public void setDistanceCharge(double distanceCharge) {
        this.distanceCharge = distanceCharge;
    }

    public double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getDurationCharge() {
        return durationCharge;
    }

    public void setDurationCharge(double durationCharge) {
        this.durationCharge = durationCharge;
    }

    public double getVehicleMultiplier() {
        return vehicleMultiplier;
    }

    public void setVehicleMultiplier(double vehicleMultiplier) {
        this.vehicleMultiplier = vehicleMultiplier;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public void setSurgeMultiplier(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
