package com.ridelink.fare.dto;

import com.ridelink.fare.model.FareCalculationBreakdown;
import com.ridelink.fare.model.PaymentMethod;
import com.ridelink.fare.model.PaymentRecord;
import com.ridelink.fare.model.PaymentStatus;

import java.time.Instant;

public class ReceiptResponse {

    private String receiptId;
    private String transactionReference;
    private String rideId;
    private String passengerId;
    private String driverId;
    private double totalAmount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private FareCalculationBreakdown breakdown;
    private Instant paymentDate;

    public ReceiptResponse() {
    }

    public static ReceiptResponse fromPayment(PaymentRecord record) {
        ReceiptResponse r = new ReceiptResponse();
        r.setReceiptId("RCP-" + record.getId());
        r.setTransactionReference(record.getTransactionReference());
        r.setRideId(record.getRideId());
        r.setPassengerId(record.getPassengerId());
        r.setDriverId(record.getDriverId());
        r.setTotalAmount(record.getAmount());
        r.setCurrency(record.getCurrency());
        r.setPaymentMethod(record.getPaymentMethod());
        r.setStatus(record.getStatus());
        r.setBreakdown(record.getBreakdown());
        r.setPaymentDate(record.getCreatedAt());
        return r;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
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
    }

    public FareCalculationBreakdown getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(FareCalculationBreakdown breakdown) {
        this.breakdown = breakdown;
    }

    public Instant getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }
}
