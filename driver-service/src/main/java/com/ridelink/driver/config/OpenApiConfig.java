package com.ridelink.driver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI driverServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Driver & Vehicle Service API")
                        .description("Microservice managing driver profiles, vehicle registrations, operational statuses, GPS coordinates, and eligible driver discovery. (Owner: Member 2)")
                        .version("1.0.0")
                        .contact(new Contact().name("RideLink Architecture Team - Member 2").email("member2@ridelink.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
