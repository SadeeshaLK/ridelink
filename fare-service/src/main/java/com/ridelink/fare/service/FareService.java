package com.ridelink.fare.service;

import com.ridelink.fare.dto.FareEstimateRequest;
import com.ridelink.fare.dto.FareEstimateResponse;
import com.ridelink.fare.dto.FinalFareCalculationRequest;
import com.ridelink.fare.dto.FinalFareResponse;

public interface FareService {
    FareEstimateResponse estimateFare(FareEstimateRequest request);
    FinalFareResponse calculateFinalFare(FinalFareCalculationRequest request);
}
