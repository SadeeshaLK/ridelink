package com.ridelink.fare.dto;

import com.ridelink.fare.model.PaymentMethod;
import com.ridelink.fare.model.PaymentStatus;

import java.time.Instant;

public class PaymentResponse {

    private String paymentId;
    private String rideId;
    private String transactionReference;
    private PaymentStatus status;
    private double amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private String message;
    private Instant timestamp;

    public PaymentResponse() {
    }

    public PaymentResponse(String paymentId, String rideId, String transactionReference, PaymentStatus status, double amount, String currency, PaymentMethod paymentMethod, String message, Instant timestamp) {
        this.paymentId = paymentId;
        this.rideId = rideId;
        this.transactionReference = transactionReference;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
