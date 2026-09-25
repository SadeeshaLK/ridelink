package com.ridelink.driver.dto;

import com.ridelink.driver.model.VehicleType;
import jakarta.validation.constraints.NotNull;

public class EligibleDriverSearchRequest {

    private double pickupLatitude;
    private double pickupLongitude;
    private VehicleType vehicleType;
    private double maxRadiusKm = 25.0; // default search radius

    public EligibleDriverSearchRequest() {
    }

    public EligibleDriverSearchRequest(double pickupLatitude, double pickupLongitude, VehicleType vehicleType, double maxRadiusKm) {
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.vehicleType = vehicleType;
        this.maxRadiusKm = maxRadiusKm > 0 ? maxRadiusKm : 25.0;
    }

    public double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getMaxRadiusKm() {
        return maxRadiusKm;
    }

    public void setMaxRadiusKm(double maxRadiusKm) {
        this.maxRadiusKm = maxRadiusKm;
    }
}
