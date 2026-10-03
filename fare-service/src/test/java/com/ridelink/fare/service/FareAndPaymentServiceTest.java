package com.ridelink.fare.service;

import com.ridelink.fare.dto.*;
import com.ridelink.fare.exception.PaymentFailedException;
import com.ridelink.fare.model.PaymentMethod;
import com.ridelink.fare.model.PaymentRecord;
import com.ridelink.fare.model.PaymentStatus;
import com.ridelink.fare.model.VehicleType;
import com.ridelink.fare.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareAndPaymentServiceTest {

    private FareServiceImpl fareService;

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        fareService = new FareServiceImpl(150.00, 80.00, 5.00, "LKR");
        paymentService = new PaymentServiceImpl(paymentRepository, "LKR");
    }

    @Test
    @DisplayName("Should estimate fare accurately according to documented formula")
    void testEstimateFare() {
        // 10 km, SEDAN (multiplier 1.2)
        FareEstimateRequest request = new FareEstimateRequest(10.0, VehicleType.SEDAN);

        FareEstimateResponse response = fareService.estimateFare(request);

        assertThat(response).isNotNull();
        assertThat(response.getVehicleType()).isEqualTo(VehicleType.SEDAN);
        assertThat(response.getEstimatedDistanceKm()).isEqualTo(10.0);
        assertThat(response.getEstimatedFare()).isGreaterThan(150.0);
        assertThat(response.getCalculationRule()).contains("Fare =");
    }

    @Test
    @DisplayName("Should calculate final fare correctly upon ride completion")
    void testCalculateFinalFare() {
        FinalFareCalculationRequest request = new FinalFareCalculationRequest("ride-123", 5.0, 15.0, VehicleType.HATCHBACK);

        FinalFareResponse response = fareService.calculateFinalFare(request);

        assertThat(response.getRideId()).isEqualTo("ride-123");
        // Base(150) + Dist(5 * 80 = 400) + Duration(15 * 5 = 75) = 625 * Hatchback(1.0) = 625.0
        assertThat(response.getFinalFare()).isEqualTo(625.0);
    }

    @Test
    @DisplayName("Should process simulated payment successfully")
    void testProcessPaymentSuccess() {
        PaymentRequest request = new PaymentRequest("ride-123", "passenger-1", "driver-1", 625.0, PaymentMethod.CARD);

        PaymentRecord savedRecord = new PaymentRecord();
        savedRecord.setId("pay-001");
        savedRecord.setRideId("ride-123");
        savedRecord.setAmount(625.0);
        savedRecord.setPaymentMethod(PaymentMethod.CARD);
        savedRecord.setStatus(PaymentStatus.PAID);
        savedRecord.setTransactionReference("TXN-12345");

        when(paymentRepository.save(any(PaymentRecord.class))).thenReturn(savedRecord);

        PaymentResponse response = paymentService.processPayment(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(response.getTransactionReference()).isEqualTo("TXN-12345");
        verify(paymentRepository, times(1)).save(any(PaymentRecord.class));
    }

    @Test
    @DisplayName("Should throw PaymentFailedException when simulateFailure is triggered (Negative workflow)")
    void testProcessPaymentFailure() {
        PaymentRequest request = new PaymentRequest("ride-123", "passenger-1", "driver-1", 625.0, PaymentMethod.CARD, true);

        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Payment failed");

        verify(paymentRepository, times(1)).save(any(PaymentRecord.class));
    }

    @Test
    @DisplayName("Should retrieve payment receipt by ride ID")
    void testGetReceiptByRideId() {
        PaymentRecord record = new PaymentRecord();
        record.setId("pay-999");
        record.setRideId("ride-123");
        record.setAmount(500.0);
        record.setCurrency("LKR");
        record.setStatus(PaymentStatus.PAID);
        record.setPaymentMethod(PaymentMethod.CASH);
        record.setTransactionReference("TXN-99999");

        when(paymentRepository.findByRideId("ride-123")).thenReturn(Optional.of(record));

        ReceiptResponse receipt = paymentService.getReceiptByRideId("ride-123");

        assertThat(receipt).isNotNull();
        assertThat(receipt.getReceiptId()).isEqualTo("RCP-pay-999");
        assertThat(receipt.getTotalAmount()).isEqualTo(500.0);
    }
}
