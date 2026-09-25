package com.ridelink.fare.repository;

import com.ridelink.fare.model.PaymentRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentRecord, String> {
    Optional<PaymentRecord> findByRideId(String rideId);
    Optional<PaymentRecord> findByTransactionReference(String transactionReference);
    List<PaymentRecord> findByPassengerId(String passengerId);
    List<PaymentRecord> findByDriverId(String driverId);
}
