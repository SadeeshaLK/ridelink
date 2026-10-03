package com.ridelink.fare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fareServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Fare & Payment Service API")
                        .description("Microservice managing upfront fare estimation, final fare calculation via documented pricing rules, and simulated payment processing with receipt generation. (Owner: Member 4)")
                        .version("1.0.0")
                        .contact(new Contact().name("RideLink Architecture Team - Member 4").email("member4@ridelink.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
