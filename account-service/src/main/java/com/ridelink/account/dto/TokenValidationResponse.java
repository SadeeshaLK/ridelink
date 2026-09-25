package com.ridelink.account.dto;

import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;

public class TokenValidationResponse {

    private boolean valid;
    private String userId;
    private String email;
    private Role role;
    private AccountStatus status;
    private String message;

    public TokenValidationResponse() {
    }

    public TokenValidationResponse(boolean valid, String userId, String email, Role role, AccountStatus status, String message) {
        this.valid = valid;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.message = message;
    }

    public static TokenValidationResponse invalid(String message) {
        TokenValidationResponse response = new TokenValidationResponse();
        response.setValid(false);
        response.setMessage(message);
        return response;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
