package com.ridelink.fare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payments")
public class PaymentRecord {

    @Id
    private String id;

    @Indexed
    private String rideId;

    private String passengerId;
    private String driverId;
    private double amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionReference;
    private String failureReason;
    private FareCalculationBreakdown breakdown;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public PaymentRecord() {
    }

    public PaymentRecord(String rideId, String passengerId, String driverId, double amount, String currency, PaymentMethod paymentMethod, PaymentStatus status, String transactionReference, FareCalculationBreakdown breakdown) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionReference = transactionReference;
        this.breakdown = breakdown;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
        this.updatedAt = Instant.now();
    }

    public FareCalculationBreakdown getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(FareCalculationBreakdown breakdown) {
        this.breakdown = breakdown;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
