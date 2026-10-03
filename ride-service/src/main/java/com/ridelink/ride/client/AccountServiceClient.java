package com.ridelink.ride.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AccountServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceClient.class);

    private final RestClient restClient;

    public AccountServiceClient(RestClient.Builder builder,
                                @Value("${services.account-service.url:http://localhost:8081}") String accountServiceUrl) {
        this.restClient = builder.baseUrl(accountServiceUrl).build();
    }

    public boolean isPassengerValid(String passengerId) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("/api/users/{id}", passengerId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    String status = (String) data.get("status");
                    return "ACTIVE".equalsIgnoreCase(status);
                }
            }
        } catch (Exception e) {
            log.warn("Account service check failed for passenger {}: {}. Allowing optimistic continuation.", passengerId, e.getMessage());
            return true; // Resilience fallback
        }
        return false;
    }
}
