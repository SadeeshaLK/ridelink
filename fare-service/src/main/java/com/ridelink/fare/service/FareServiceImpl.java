package com.ridelink.fare.service;

import com.ridelink.fare.dto.FareEstimateRequest;
import com.ridelink.fare.dto.FareEstimateResponse;
import com.ridelink.fare.dto.FinalFareCalculationRequest;
import com.ridelink.fare.dto.FinalFareResponse;
import com.ridelink.fare.model.FareCalculationBreakdown;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FareServiceImpl implements FareService {

    private final double baseFare;
    private final double ratePerKm;
    private final double ratePerMinute;
    private final String currency;

    public FareServiceImpl(
            @Value("${fare.base-fare:150.00}") double baseFare,
            @Value("${fare.rate-per-km:80.00}") double ratePerKm,
            @Value("${fare.rate-per-minute:5.00}") double ratePerMinute,
            @Value("${fare.currency:LKR}") String currency) {
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.ratePerMinute = ratePerMinute;
        this.currency = currency;
    }

    @Override
    public FareEstimateResponse estimateFare(FareEstimateRequest request) {
        double distanceKm = request.getEstimatedDistanceKm();
        // Assume average city speed 25 km/h -> duration = (distance / 25) * 60 minutes
        double estimatedDurationMinutes = Math.max(5.0, (distanceKm / 25.0) * 60.0);

        double distanceCharge = round(distanceKm * ratePerKm);
        double durationCharge = round(estimatedDurationMinutes * ratePerMinute);
        double vehicleMultiplier = request.getVehicleType().getMultiplier();
        double surge = request.getSurgeMultiplier() > 0 ? request.getSurgeMultiplier() : 1.0;

        double subtotal = (baseFare + distanceCharge + durationCharge) * vehicleMultiplier * surge;
        double totalFare = round(subtotal);

        FareCalculationBreakdown breakdown = new FareCalculationBreakdown(
                baseFare,
                round(distanceKm),
                distanceCharge,
                round(estimatedDurationMinutes),
                durationCharge,
                vehicleMultiplier,
                surge,
                totalFare,
                currency
        );

        String rule = String.format("Fare = (Base(%.2f) + Distance(%.2f km * %.2f) + Duration(%.2f min * %.2f)) * VehicleMult(%.1f) * Surge(%.1f)",
                baseFare, distanceKm, ratePerKm, estimatedDurationMinutes, ratePerMinute, vehicleMultiplier, surge);

        return new FareEstimateResponse(
                request.getVehicleType(),
                round(distanceKm),
                round(estimatedDurationMinutes),
                totalFare,
                currency,
                rule,
                breakdown
        );
    }

    @Override
    public FinalFareResponse calculateFinalFare(FinalFareCalculationRequest request) {
        double distanceKm = request.getActualDistanceKm();
        double durationMinutes = request.getActualDurationMinutes();

        double distanceCharge = round(distanceKm * ratePerKm);
        double durationCharge = round(durationMinutes * ratePerMinute);
        double vehicleMultiplier = request.getVehicleType().getMultiplier();
        double surge = request.getSurgeMultiplier() > 0 ? request.getSurgeMultiplier() : 1.0;

        double subtotal = (baseFare + distanceCharge + durationCharge) * vehicleMultiplier * surge;
        double finalFare = Math.max(baseFare * vehicleMultiplier, round(subtotal - request.getDiscountAmount()));

        FareCalculationBreakdown breakdown = new FareCalculationBreakdown(
                baseFare,
                round(distanceKm),
                distanceCharge,
                round(durationMinutes),
                durationCharge,
                vehicleMultiplier,
                surge,
                finalFare,
                currency
        );

        String rule = String.format("FinalFare = max(Base(%.2f) * VehicleMult(%.1f), (Base + DistanceCharge(%.2f) + DurationCharge(%.2f)) * VehicleMult(%.1f) * Surge(%.1f) - Discount(%.2f))",
                baseFare, vehicleMultiplier, distanceCharge, durationCharge, vehicleMultiplier, surge, request.getDiscountAmount());

        return new FinalFareResponse(
                request.getRideId(),
                finalFare,
                currency,
                rule,
                breakdown
        );
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
