package com.ridelink.fare.controller;

import com.ridelink.fare.dto.*;
import com.ridelink.fare.service.FareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fares")
@Tag(name = "Fare Estimation and Calculation", description = "Endpoints for estimating upfront ride fares and computing final fares with documented rules")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/estimate")
    @Operation(summary = "Estimate ride fare", description = "Calculates estimated fare based on distance, duration, and vehicle tier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fare estimated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid distance or vehicle type")
    })
    public ResponseEntity<com.ridelink.fare.dto.ApiResponse<FareEstimateResponse>> estimateFare(
            @Valid @RequestBody FareEstimateRequest request) {
        FareEstimateResponse estimate = fareService.estimateFare(request);
        return ResponseEntity.ok(com.ridelink.fare.dto.ApiResponse.success("Fare estimated successfully", estimate));
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate final fare", description = "Calculates final fare upon ride completion using documented pricing rules")
    public ResponseEntity<com.ridelink.fare.dto.ApiResponse<FinalFareResponse>> calculateFinalFare(
            @Valid @RequestBody FinalFareCalculationRequest request) {
        FinalFareResponse finalFare = fareService.calculateFinalFare(request);
        return ResponseEntity.ok(com.ridelink.fare.dto.ApiResponse.success("Final fare calculated", finalFare));
    }
}
