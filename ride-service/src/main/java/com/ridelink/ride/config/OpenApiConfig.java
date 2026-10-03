package com.ridelink.ride.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rideServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Ride Management Service API")
                        .description("Microservice orchestrating ride bookings, proximity driver dispatching, full lifecycle state transitions (REQUESTED -> ASSIGNED -> ACCEPTED -> IN_PROGRESS -> COMPLETED/CANCELLED), and interservice coordination. (Owner: Member 3)")
                        .version("1.0.0")
                        .contact(new Contact().name("RideLink Architecture Team - Member 3").email("member3@ridelink.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
