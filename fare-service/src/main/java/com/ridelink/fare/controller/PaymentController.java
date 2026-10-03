package com.ridelink.fare.controller;

import com.ridelink.fare.dto.ApiResponse;
import com.ridelink.fare.dto.PaymentRequest;
import com.ridelink.fare.dto.PaymentResponse;
import com.ridelink.fare.dto.ReceiptResponse;
import com.ridelink.fare.model.PaymentRecord;
import com.ridelink.fare.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment and Receipts", description = "Endpoints for simulated payment processing, transaction records, and receipt retrieval")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Process simulated payment", description = "Records a simulated payment. Set simulateFailure=true to test payment failure workflow.")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed successfully", response));
    }

    @GetMapping("/receipt/ride/{rideId}")
    @Operation(summary = "Retrieve payment receipt by Ride ID")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptByRideId(@PathVariable String rideId) {
        ReceiptResponse receipt = paymentService.getReceiptByRideId(rideId);
        return ResponseEntity.ok(ApiResponse.success("Receipt retrieved", receipt));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve receipt by Payment ID")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptById(@PathVariable String id) {
        ReceiptResponse receipt = paymentService.getReceiptById(id);
        return ResponseEntity.ok(ApiResponse.success("Receipt retrieved", receipt));
    }

    @GetMapping("/passenger/{passengerId}")
    @Operation(summary = "Get payment history for a passenger")
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getPaymentsByPassenger(@PathVariable String passengerId) {
        List<PaymentRecord> payments = paymentService.getPaymentsByPassengerId(passengerId);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved", payments));
    }
}
