package com.ridelink.ride.client;

import com.ridelink.ride.dto.FareEstimateClientResponse;
import com.ridelink.ride.dto.FinalFareClientResponse;
import com.ridelink.ride.model.VehicleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class FareServiceClient {

    private static final Logger log = LoggerFactory.getLogger(FareServiceClient.class);

    private final RestClient restClient;

    public FareServiceClient(RestClient.Builder builder,
                             @Value("${services.fare-service.url:http://localhost:8084}") String fareServiceUrl) {
        this.restClient = builder.baseUrl(fareServiceUrl).build();
    }

    public FareEstimateClientResponse estimateFare(double distanceKm, VehicleType vehicleType) {
        try {
            Map<String, Object> body = Map.of(
                    "estimatedDistanceKm", distanceKm,
                    "vehicleType", vehicleType.name(),
                    "surgeMultiplier", 1.0
            );

            Map<String, Object> response = restClient.post()
                    .uri("/api/fares/estimate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    FareEstimateClientResponse resp = new FareEstimateClientResponse();
                    if (data.get("estimatedFare") != null) {
                        resp.setEstimatedFare(((Number) data.get("estimatedFare")).doubleValue());
                    }
                    if (data.get("estimatedDistanceKm") != null) {
                        resp.setEstimatedDistanceKm(((Number) data.get("estimatedDistanceKm")).doubleValue());
                    }
                    resp.setCurrency((String) data.get("currency"));
                    resp.setCalculationRule((String) data.get("calculationRule"));
                    return resp;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to obtain fare estimate from Fare Service: {}. Using fallback calculation.", e.getMessage());
        }

        // Resilient fallback formula if Fare Service is offline during testing
        FareEstimateClientResponse fallback = new FareEstimateClientResponse();
        fallback.setEstimatedDistanceKm(distanceKm);
        double fallbackFare = (150.0 + (distanceKm * 80.0)) * 1.2;
        fallback.setEstimatedFare(Math.round(fallbackFare * 100.0) / 100.0);
        fallback.setCurrency("LKR");
        fallback.setCalculationRule("Fallback estimation formula");
        return fallback;
    }

    public FinalFareClientResponse calculateFinalFare(String rideId, double actualDistanceKm, double actualDurationMinutes, VehicleType vehicleType) {
        try {
            Map<String, Object> body = Map.of(
                    "rideId", rideId,
                    "actualDistanceKm", actualDistanceKm,
                    "actualDurationMinutes", actualDurationMinutes,
                    "vehicleType", vehicleType.name()
            );

            Map<String, Object> response = restClient.post()
                    .uri("/api/fares/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    FinalFareClientResponse resp = new FinalFareClientResponse();
                    resp.setRideId((String) data.get("rideId"));
                    if (data.get("finalFare") != null) {
                        resp.setFinalFare(((Number) data.get("finalFare")).doubleValue());
                    }
                    resp.setCurrency((String) data.get("currency"));
                    resp.setCalculationRule((String) data.get("calculationRule"));
                    return resp;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to calculate final fare from Fare Service: {}. Using fallback.", e.getMessage());
        }

        FinalFareClientResponse fallback = new FinalFareClientResponse();
        fallback.setRideId(rideId);
        double fallbackFare = (150.0 + (actualDistanceKm * 80.0) + (actualDurationMinutes * 5.0)) * 1.2;
        fallback.setFinalFare(Math.round(fallbackFare * 100.0) / 100.0);
        fallback.setCurrency("LKR");
        return fallback;
    }

    public String processPayment(String rideId, String passengerId, String driverId, double amount, String paymentMethod) {
        try {
            Map<String, Object> body = Map.of(
                    "rideId", rideId,
                    "passengerId", passengerId,
                    "driverId", driverId != null ? driverId : "UNKNOWN",
                    "amount", amount,
                    "paymentMethod", paymentMethod
            );

            Map<String, Object> response = restClient.post()
                    .uri("/api/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    return (String) data.get("paymentId");
                }
            }
        } catch (Exception e) {
            log.warn("Payment recording interservice call failed: {}", e.getMessage());
        }
        return "PAY-SIMULATED-" + rideId;
    }
}
