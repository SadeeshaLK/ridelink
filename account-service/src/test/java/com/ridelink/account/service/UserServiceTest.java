package com.ridelink.account.service;

import com.ridelink.account.dto.AuthResponse;
import com.ridelink.account.dto.LoginRequest;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.exception.InvalidCredentialsException;
import com.ridelink.account.exception.UserAlreadyExistsException;
import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("passenger@example.com", "encodedPassword", "John Doe", "0771234567", Role.ROLE_PASSENGER);
        sampleUser.setId("user-123");
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest("passenger@example.com", "Secret123", "John Doe", "0771234567", Role.ROLE_PASSENGER);

        when(userRepository.existsByEmail("passenger@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateToken(any(User.class))).thenReturn("jwt-token-sample");

        AuthResponse response = userService.register(req);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-sample");
        assertThat(response.getEmail()).isEqualTo("passenger@example.com");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_PASSENGER);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with already used email")
    void testRegisterDuplicateEmail() {
        RegisterRequest req = new RegisterRequest("passenger@example.com", "Secret123", "John Doe", "0771234567", Role.ROLE_PASSENGER);

        when(userRepository.existsByEmail("passenger@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void testLoginSuccess() {
        LoginRequest req = new LoginRequest("passenger@example.com", "Secret123");

        when(userRepository.findByEmail("passenger@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Secret123", "encodedPassword")).thenReturn(true);
        when(tokenProvider.generateToken(sampleUser)).thenReturn("jwt-token-sample");

        AuthResponse response = userService.login(req);

        assertThat(response.getToken()).isEqualTo("jwt-token-sample");
        assertThat(response.getUserId()).isEqualTo("user-123");
    }

    @Test
    @DisplayName("Should throw exception when password does not match")
    void testLoginInvalidPassword() {
        LoginRequest req = new LoginRequest("passenger@example.com", "WrongPassword");

        when(userRepository.findByEmail("passenger@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("WrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(req))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    @DisplayName("Should update user status to SUSPENDED")
    void testUpdateStatus() {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileResponse response = userService.updateStatus("user-123", AccountStatus.SUSPENDED);

        assertThat(response.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
        verify(userRepository).save(sampleUser);
    }
}
