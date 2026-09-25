package com.ridelink.account.controller;

import com.ridelink.account.dto.*;
import com.ridelink.account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token validation")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a passenger, driver, or admin account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation error in request payload"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<com.ridelink.account.dto.ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.ridelink.account.dto.ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates credentials and returns a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account suspended or deactivated")
    })
    public ResponseEntity<com.ridelink.account.dto.ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(com.ridelink.account.dto.ApiResponse.success("Login successful", response));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Verifies token validity and returns user identity (for interservice communication)")
    public ResponseEntity<com.ridelink.account.dto.ApiResponse<TokenValidationResponse>> validateToken(
            @RequestParam String token) {
        TokenValidationResponse response = userService.validateToken(token);
        if (response.isValid()) {
            return ResponseEntity.ok(com.ridelink.account.dto.ApiResponse.success("Token is valid", response));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(com.ridelink.account.dto.ApiResponse.error(response.getMessage()));
        }
    }
}
