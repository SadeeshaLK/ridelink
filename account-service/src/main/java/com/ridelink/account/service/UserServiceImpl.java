package com.ridelink.account.service;

import com.ridelink.account.dto.*;
import com.ridelink.account.exception.AccountDisabledException;
import com.ridelink.account.exception.InvalidCredentialsException;
import com.ridelink.account.exception.ResourceNotFoundException;
import com.ridelink.account.exception.UserAlreadyExistsException;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public UserServiceImpl(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getPhoneNumber(),
                request.getRole()
        );

        User saved = userRepository.save(user);
        String token = tokenProvider.generateToken(saved);

        return new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getFullName(), saved.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountDisabledException("Account is " + user.getStatus() + ". Please contact support.");
        }

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            return TokenValidationResponse.invalid("Token is invalid or expired");
        }

        String userId = tokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return TokenValidationResponse.invalid("User associated with token not found");
        }

        if (user.getStatus() != AccountStatus.ACTIVE) {
            return TokenValidationResponse.invalid("Account is " + user.getStatus());
        }

        return new TokenValidationResponse(true, user.getId(), user.getEmail(), user.getRole(), user.getStatus(), "Token is valid");
    }

    @Override
    public UserProfileResponse getProfileById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToProfile(user);
    }

    @Override
    public UserProfileResponse updateProfile(String id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        User updated = userRepository.save(user);

        return mapToProfile(updated);
    }

    @Override
    public UserProfileResponse updateStatus(String id, AccountStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(status);
        User updated = userRepository.save(user);

        return mapToProfile(updated);
    }

    @Override
    public List<UserProfileResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToProfile)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToProfile)
                .collect(Collectors.toList());
    }

    private UserProfileResponse mapToProfile(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
