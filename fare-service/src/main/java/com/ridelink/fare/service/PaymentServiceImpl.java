package com.ridelink.fare.service;

import com.ridelink.fare.dto.PaymentRequest;
import com.ridelink.fare.dto.PaymentResponse;
import com.ridelink.fare.dto.ReceiptResponse;
import com.ridelink.fare.exception.PaymentFailedException;
import com.ridelink.fare.exception.ResourceNotFoundException;
import com.ridelink.fare.model.PaymentRecord;
import com.ridelink.fare.model.PaymentStatus;
import com.ridelink.fare.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final String currency;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              @Value("${fare.currency:LKR}") String currency) {
        this.paymentRepository = paymentRepository;
        this.currency = currency;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        String txRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PaymentRecord record = new PaymentRecord();
        record.setRideId(request.getRideId());
        record.setPassengerId(request.getPassengerId());
        record.setDriverId(request.getDriverId());
        record.setAmount(request.getAmount());
        record.setCurrency(currency);
        record.setPaymentMethod(request.getPaymentMethod());
        record.setTransactionReference(txRef);
        record.setCreatedAt(Instant.now());
        record.setUpdatedAt(Instant.now());

        // Negative workflow demonstration check
        if (request.isSimulateFailure()) {
            record.setStatus(PaymentStatus.FAILED);
            record.setFailureReason("Simulated payment gateway declined: Insufficient funds or card issuer error");
            paymentRepository.save(record);
            throw new PaymentFailedException("Payment failed: " + record.getFailureReason() + " (Ref: " + txRef + ")");
        }

        record.setStatus(PaymentStatus.PAID);
        PaymentRecord saved = paymentRepository.save(record);

        return new PaymentResponse(
                saved.getId(),
                saved.getRideId(),
                saved.getTransactionReference(),
                saved.getStatus(),
                saved.getAmount(),
                saved.getCurrency(),
                saved.getPaymentMethod(),
                "Payment successfully processed via " + saved.getPaymentMethod(),
                saved.getCreatedAt()
        );
    }

    @Override
    public ReceiptResponse getReceiptByRideId(String rideId) {
        PaymentRecord record = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment receipt found for ride ID: " + rideId));
        return ReceiptResponse.fromPayment(record);
    }

    @Override
    public ReceiptResponse getReceiptById(String paymentId) {
        PaymentRecord record = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with ID: " + paymentId));
        return ReceiptResponse.fromPayment(record);
    }

    @Override
    public List<PaymentRecord> getPaymentsByPassengerId(String passengerId) {
        return paymentRepository.findByPassengerId(passengerId);
    }

    @Override
    public List<PaymentRecord> getAllPayments() {
        return paymentRepository.findAll();
    }
}
