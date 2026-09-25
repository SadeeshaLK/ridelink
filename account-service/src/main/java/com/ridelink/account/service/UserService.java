package com.ridelink.account.service;

import com.ridelink.account.dto.*;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;

import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    TokenValidationResponse validateToken(String token);
    UserProfileResponse getProfileById(String id);
    UserProfileResponse updateProfile(String id, UpdateProfileRequest request);
    UserProfileResponse updateStatus(String id, AccountStatus status);
    List<UserProfileResponse> getUsersByRole(Role role);
    List<UserProfileResponse> getAllUsers();
}
