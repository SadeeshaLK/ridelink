package com.ridelink.account.controller;

import com.ridelink.account.dto.ApiResponse;
import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.model.Role;
import com.ridelink.account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for viewing and updating user profiles and account statuses")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        UserProfileResponse profile = userService.getProfileById(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable String id) {
        UserProfileResponse profile = userService.getProfileById(id);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse profile = userService.updateProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update account status (ACTIVE, SUSPENDED, DEACTIVATED)")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {
        UserProfileResponse profile = userService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", profile));
    }

    @GetMapping
    @Operation(summary = "Get all users (Admin operation)")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved", users));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users filtered by role")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getUsersByRole(@PathVariable Role role) {
        List<UserProfileResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved by role", users));
    }
}
