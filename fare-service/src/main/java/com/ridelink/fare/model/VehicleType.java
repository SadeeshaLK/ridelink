package com.ridelink.fare.model;

public enum VehicleType {
    SEDAN(1.2),
    SUV(1.5),
    HATCHBACK(1.0),
    MOTORBIKE(0.6);

    private final double multiplier;

    VehicleType(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
