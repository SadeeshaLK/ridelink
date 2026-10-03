package com.ridelink.fare.dto;

import com.ridelink.fare.model.FareCalculationBreakdown;

public class FinalFareResponse {

    private String rideId;
    private double finalFare;
    private String currency;
    private String calculationRule;
    private FareCalculationBreakdown breakdown;

    public FinalFareResponse() {
    }

    public FinalFareResponse(String rideId, double finalFare, String currency, String calculationRule, FareCalculationBreakdown breakdown) {
        this.rideId = rideId;
        this.finalFare = finalFare;
        this.currency = currency;
        this.calculationRule = calculationRule;
        this.breakdown = breakdown;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public double getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(double finalFare) {
        this.finalFare = finalFare;
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
