package com.ridelink.ride.dto;

import com.ridelink.ride.model.Location;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.model.VehicleType;

import java.time.Instant;

public class RideResponse {

    private String id;
    private String passengerId;
    private String driverId;
    private VehicleType vehicleType;
    private Location pickupLocation;
    private Location destinationLocation;
    private RideStatus status;
    private double estimatedDistanceKm;
    private double estimatedFare;
    private Double finalFare;
    private String paymentId;
    private String cancellationReason;
    private Instant createdAt;
    private Instant assignedAt;
    private Instant acceptedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant cancelledAt;

    public RideResponse() {
    }

    public static RideResponse fromEntity(Ride ride) {
        RideResponse r = new RideResponse();
        r.setId(ride.getId());
        r.setPassengerId(ride.getPassengerId());
        r.setDriverId(ride.getDriverId());
        r.setVehicleType(ride.getVehicleType());
        r.setPickupLocation(ride.getPickupLocation());
        r.setDestinationLocation(ride.getDestinationLocation());
        r.setStatus(ride.getStatus());
        r.setEstimatedDistanceKm(ride.getEstimatedDistanceKm());
        r.setEstimatedFare(ride.getEstimatedFare());
        r.setFinalFare(ride.getFinalFare());
        r.setPaymentId(ride.getPaymentId());
        r.setCancellationReason(ride.getCancellationReason());
        r.setCreatedAt(ride.getCreatedAt());
        r.setAssignedAt(ride.getAssignedAt());
        r.setAcceptedAt(ride.getAcceptedAt());
        r.setStartedAt(ride.getStartedAt());
        r.setCompletedAt(ride.getCompletedAt());
        r.setCancelledAt(ride.getCancelledAt());
        return r;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public double getEstimatedDistanceKm() {
        return estimatedDistanceKm;
    }

    public void setEstimatedDistanceKm(double estimatedDistanceKm) {
        this.estimatedDistanceKm = estimatedDistanceKm;
    }

    public double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public Double getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(Double finalFare) {
        this.finalFare = finalFare;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
