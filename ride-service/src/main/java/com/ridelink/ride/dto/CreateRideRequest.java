package com.ridelink.ride.dto;

import com.ridelink.ride.model.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRideRequest {

    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @NotNull(message = "Vehicle type is required (SEDAN, SUV, HATCHBACK, MOTORBIKE)")
    private VehicleType vehicleType;

    @Valid
    @NotNull(message = "Pickup location is required")
    private LocationDto pickupLocation;

    @Valid
    @NotNull(message = "Destination location is required")
    private LocationDto destinationLocation;

    public CreateRideRequest() {
    }

    public CreateRideRequest(String passengerId, VehicleType vehicleType, LocationDto pickupLocation, LocationDto destinationLocation) {
        this.passengerId = passengerId;
        this.vehicleType = vehicleType;
        this.pickupLocation = pickupLocation;
        this.destinationLocation = destinationLocation;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public LocationDto getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(LocationDto pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public LocationDto getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(LocationDto destinationLocation) {
        this.destinationLocation = destinationLocation;
    }
}
