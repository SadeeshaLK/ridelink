package com.ridelink.fare.service;

import com.ridelink.fare.dto.PaymentRequest;
import com.ridelink.fare.dto.PaymentResponse;
import com.ridelink.fare.dto.ReceiptResponse;
import com.ridelink.fare.model.PaymentRecord;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    ReceiptResponse getReceiptByRideId(String rideId);
    ReceiptResponse getReceiptById(String paymentId);
    List<PaymentRecord> getPaymentsByPassengerId(String passengerId);
    List<PaymentRecord> getAllPayments();
}
