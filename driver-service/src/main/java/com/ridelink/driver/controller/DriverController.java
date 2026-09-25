package com.ridelink.driver.controller;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.model.DriverStatus;
import com.ridelink.driver.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Driver and Vehicle Management", description = "Endpoints for driver profiles, vehicle details, availability, and proximity dispatching")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @Operation(summary = "Register driver profile and vehicle", description = "Creates a driver operational profile linked to a user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Driver profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "409", description = "Driver profile already exists for user ID")
    })
    public ResponseEntity<com.ridelink.driver.dto.ApiResponse<DriverResponse>> createProfile(
            @Valid @RequestBody CreateDriverProfileRequest request) {
        DriverResponse response = driverService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.ridelink.driver.dto.ApiResponse.success("Driver profile created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver profile by Driver ID")
    public ResponseEntity<com.ridelink.driver.dto.ApiResponse<DriverResponse>> getProfileById(@PathVariable String id) {
        DriverResponse response = driverService.getProfileById(id);
        return ResponseEntity.ok(com.ridelink.driver.dto.ApiResponse.success("Driver retrieved", response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get driver profile by User ID (from Account Service)")
    public ResponseEntity<com.ridelink.driver.dto.ApiResponse<DriverResponse>> getProfileByUserId(@PathVariable String userId) {
        DriverResponse response = driverService.getProfileByUserId(userId);
        return ResponseEntity.ok(com.ridelink.driver.dto.ApiResponse.success("Driver retrieved by user id", response));
    }

    @PutMapping("/{id}/location")
    @Operation(summary = "Update driver simulated location", description = "Updates driver's GPS coordinates and location name")
    public ResponseEntity<com.ridelink.driver.dto.ApiResponse<DriverResponse>> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody UpdateLocationRequest request) {
        DriverResponse response = driverService.updateLocation(id, request);
        return ResponseEntity.ok(com.ridelink.driver.dto.ApiResponse.success("Location updated successfully", response));
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Update driver availability status", description = "Toggles driver state between AVAILABLE, BUSY, or OFFLINE")
    public ResponseEntity<com.ridelink.driver.dto.ApiResponse<DriverResponse>> updateAvailability(
            @PathVariable String id,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        DriverResponse response = driverService.updateAvailability(id, request);
        return ResponseEntity.ok(com.ridelink.driver.dto.ApiResponse.success("Availability updated successfully", response));
    }

    @PostMapping("/eligible")
    @Operation(summary = "Find eligible available drivers", description = "Finds AVAILABLE drivers matching vehicle type and ranks them by distance to pickup")
    public ResponseEntity<com.ridelink.driver.dto.ApiResponse<List<EligibleDriverResponse>>> findEligibleDrivers(
            @RequestBody EligibleDriverSearchRequest request) {
        List<EligibleDriverResponse> eligibleDrivers = driverService.findEligibleDrivers(request);
        return ResponseEntity.ok(com.ridelink.driver.dto.ApiResponse.success(
                "Found " + eligibleDrivers.size() + " eligible driver(s)", eligibleDrivers));
    }

    @GetMapping
    @Operation(summary = "Get all drivers or filter by status")
    public ResponseEntity<com.ridelink.driver.dto.ApiResponse<List<DriverResponse>>> getDrivers(
            @RequestParam(required = false) DriverStatus status) {
        List<DriverResponse> drivers = (status != null)
                ? driverService.getDriversByStatus(status)
                : driverService.getAllDrivers();
        return ResponseEntity.ok(com.ridelink.driver.dto.ApiResponse.success("Drivers retrieved", drivers));
    }
}
