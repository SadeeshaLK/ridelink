package com.ridelink.account.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
