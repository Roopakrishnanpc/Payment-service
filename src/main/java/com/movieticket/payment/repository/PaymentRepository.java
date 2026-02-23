package com.movieticket.payment.repository;

import com.movieticket.payment.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByBookingId(Long bookingId);
}
