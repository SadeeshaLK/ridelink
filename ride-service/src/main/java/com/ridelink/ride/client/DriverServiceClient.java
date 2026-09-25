package com.ridelink.ride.client;

import com.ridelink.ride.dto.EligibleDriverClientResponse;
import com.ridelink.ride.model.VehicleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DriverServiceClient {

    private static final Logger log = LoggerFactory.getLogger(DriverServiceClient.class);

    private final RestClient restClient;

    public DriverServiceClient(RestClient.Builder builder,
                               @Value("${services.driver-service.url:http://localhost:8082}") String driverServiceUrl) {
        this.restClient = builder.baseUrl(driverServiceUrl).build();
    }

    public List<EligibleDriverClientResponse> findEligibleDrivers(double lat, double lon, VehicleType vehicleType, double radiusKm) {
        try {
            Map<String, Object> body = Map.of(
                    "pickupLatitude", lat,
                    "pickupLongitude", lon,
                    "vehicleType", vehicleType.name(),
                    "maxRadiusKm", radiusKm
            );

            Map<String, Object> response = restClient.post()
                    .uri("/api/drivers/eligible")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
                if (data != null) {
                    return data.stream().map(m -> {
                        EligibleDriverClientResponse d = new EligibleDriverClientResponse();
                        d.setDriverId((String) m.get("driverId"));
                        d.setUserId((String) m.get("userId"));
                        d.setFullName((String) m.get("fullName"));
                        d.setStatus((String) m.get("status"));
                        if (m.get("rating") != null) {
                            d.setRating(((Number) m.get("rating")).doubleValue());
                        }
                        if (m.get("distanceKm") != null) {
                            d.setDistanceKm(((Number) m.get("distanceKm")).doubleValue());
                        }
                        return d;
                    }).toList();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to communicate with Driver Service: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    public void updateDriverAvailability(String driverId, String status, String activeRideId) {
        try {
            Map<String, Object> body = (activeRideId != null)
                    ? Map.of("status", status, "activeRideId", activeRideId)
                    : Map.of("status", status);

            restClient.patch()
                    .uri("/api/drivers/{id}/availability", driverId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Updated driver {} status to {}", driverId, status);
        } catch (Exception e) {
            log.warn("Could not notify Driver Service for driver {}: {}", driverId, e.getMessage());
        }
    }
}
