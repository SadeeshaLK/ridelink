package com.ridelink.ride.controller;

import com.ridelink.ride.dto.*;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.service.RideService;
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
@RequestMapping("/api/rides")
@Tag(name = "Ride Lifecycle Management", description = "Endpoints for ride requests, driver dispatching, state lifecycle transitions, and completion")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    @Operation(summary = "Create ride request", description = "Passengers submit pickup and destination details to request a ride")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ride request created"),
            @ApiResponse(responseCode = "400", description = "Invalid ride request details"),
            @ApiResponse(responseCode = "404", description = "Passenger not found or invalid account")
    })
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<RideResponse>> requestRide(
            @Valid @RequestBody CreateRideRequest request) {
        RideResponse response = rideService.requestRide(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.ridelink.ride.dto.ApiResponse.success("Ride requested successfully", response));
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign driver to ride", description = "Assigns an eligible available driver (auto-match closest driver or assign by driverId)")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<RideResponse>> assignDriver(
            @PathVariable String id,
            @RequestParam(required = false) String driverId) {
        RideResponse response = rideService.assignDriver(id, driverId);
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Driver assigned successfully", response));
    }

    @PatchMapping("/{id}/accept")
    @Operation(summary = "Driver accepts assigned ride", description = "Transitions ride status to ACCEPTED")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<RideResponse>> acceptRide(
            @PathVariable String id,
            @RequestParam(required = false) String driverId) {
        RideResponse response = rideService.acceptRide(id, driverId);
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Ride accepted by driver", response));
    }

    @PatchMapping("/{id}/start")
    @Operation(summary = "Start ride", description = "Driver marks the passenger on board; transitions status to IN_PROGRESS")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<RideResponse>> startRide(
            @PathVariable String id,
            @RequestParam(required = false) String driverId) {
        RideResponse response = rideService.startRide(id, driverId);
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Ride started", response));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete ride and process payment", description = "Calculates final fare, records payment, and releases driver to AVAILABLE")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<RideResponse>> completeRide(
            @PathVariable String id,
            @Valid @RequestBody CompleteRideRequest request) {
        RideResponse response = rideService.completeRide(id, request);
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Ride completed and payment recorded", response));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel ride", description = "Cancels active ride with a reason and releases assigned driver")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<RideResponse>> cancelRide(
            @PathVariable String id,
            @Valid @RequestBody CancelRideRequest request) {
        RideResponse response = rideService.cancelRide(id, request);
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Ride cancelled", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ride by ID")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<RideResponse>> getRideById(@PathVariable String id) {
        RideResponse response = rideService.getRideById(id);
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Ride retrieved", response));
    }

    @GetMapping("/passenger/{passengerId}")
    @Operation(summary = "Get ride history for passenger")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<List<RideResponse>>> getRidesByPassenger(@PathVariable String passengerId) {
        List<RideResponse> rides = rideService.getRidesByPassengerId(passengerId);
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Passenger rides retrieved", rides));
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get ride history for driver")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<List<RideResponse>>> getRidesByDriver(@PathVariable String driverId) {
        List<RideResponse> rides = rideService.getRidesByDriverId(driverId);
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Driver rides retrieved", rides));
    }

    @GetMapping
    @Operation(summary = "Get all rides or filter by status")
    public ResponseEntity<com.ridelink.ride.dto.ApiResponse<List<RideResponse>>> getAllRides(
            @RequestParam(required = false) RideStatus status) {
        List<RideResponse> rides = (status != null)
                ? rideService.getRidesByStatus(status)
                : rideService.getAllRides();
        return ResponseEntity.ok(com.ridelink.ride.dto.ApiResponse.success("Rides retrieved", rides));
    }
}
