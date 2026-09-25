package com.ridelink.driver.dto;

import com.ridelink.driver.model.DriverStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateAvailabilityRequest {

    @NotNull(message = "Status is required (AVAILABLE, BUSY, OFFLINE)")
    private DriverStatus status;

    private String activeRideId;

    public UpdateAvailabilityRequest() {
    }

    public UpdateAvailabilityRequest(DriverStatus status) {
        this.status = status;
    }

    public UpdateAvailabilityRequest(DriverStatus status, String activeRideId) {
        this.status = status;
        this.activeRideId = activeRideId;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public String getActiveRideId() {
        return activeRideId;
    }

    public void setActiveRideId(String activeRideId) {
        this.activeRideId = activeRideId;
    }
}
