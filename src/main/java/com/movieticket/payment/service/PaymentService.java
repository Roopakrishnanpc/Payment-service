package com.movieticket.payment.service;

import com.movieticket.payment.domain.*;
import com.movieticket.payment.dto.*;
import com.movieticket.payment.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${payment.simulate-failure:false}")
    private boolean simulateFailure;

    @Value("${payment.simulate-pending:false}")
    private boolean simulatePending;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackCharge")
    @Retry(name = "paymentService")
    public PaymentResponseDTO charge(PaymentRequestDTO request) {

        Optional<PaymentTransaction> existing =
                paymentRepository.findByBookingId(request.getBookingId());

        // =========================================
        // IDEMPOTENT CASE
        // =========================================
        if (existing.isPresent()) {

            PaymentTransaction tx = existing.get();

            log.warn("PAYMENT_IDEMPOTENT bookingId={} status={}",
                    request.getBookingId(),
                    tx.getStatus());

            return buildResponse(tx);
        }

        // =========================================
        // SIMULATED FAILURE
        // =========================================
        if (simulateFailure) {
            return saveAndReturn(request, PaymentStatus.FAILED);
        }

        // =========================================
        // SIMULATED PENDING (Optional)
        // =========================================
        if (simulatePending) {
            return saveAndReturn(request, PaymentStatus.PENDING);
        }

        // =========================================
        // RANDOM SUCCESS / FAILURE
        // =========================================
        boolean success = new Random().nextInt(10) < 8;

        PaymentStatus status =
                success ? PaymentStatus.CHARGED
                        : PaymentStatus.FAILED;

        return saveAndReturn(request, status);
    }

    // ===================================================
    // COMMON SAVE LOGIC
    // ===================================================
    private PaymentResponseDTO saveAndReturn(
            PaymentRequestDTO request,
            PaymentStatus status) {

        PaymentTransaction transaction = paymentRepository.save(
                PaymentTransaction.builder()
                        .bookingId(request.getBookingId())
                        .userId(request.getUserId())
                        .amount(request.getAmount())
                        .status(status)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        log.info("PAYMENT_SAVED bookingId={} status={} txId={}",
                request.getBookingId(),
                status,
                transaction.getId());

        return buildResponse(transaction);
    }

    private PaymentResponseDTO buildResponse(PaymentTransaction tx) {

        return PaymentResponseDTO.builder()
                .bookingId(tx.getBookingId())
                .transactionId(tx.getId())
                .status(tx.getStatus().name())
                .success(tx.getStatus() == PaymentStatus.CHARGED)
                .build();
    }

    // ===================================================
    // FALLBACK
    // ===================================================
    public PaymentResponseDTO fallbackCharge(
            PaymentRequestDTO request,
            Throwable ex) {

        log.error("PAYMENT_FALLBACK_TRIGGERED bookingId={} reason={}",
                request.getBookingId(),
                ex.getMessage());

        return saveAndReturn(request, PaymentStatus.FAILED);
    }

    // ===================================================
    // REFUND
    // ===================================================
    public PaymentResponseDTO refund(PaymentRequestDTO request) {

        PaymentTransaction transaction =
                paymentRepository.findByBookingId(request.getBookingId())
                        .orElseThrow(() ->
                                new RuntimeException("Payment not found")
                        );

        if (transaction.getStatus() == PaymentStatus.REFUNDED) {
            return buildResponse(transaction);
        }

        if (transaction.getStatus() != PaymentStatus.CHARGED) {
            return buildResponse(transaction);
        }

        transaction.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(transaction);

        log.info("PAYMENT_REFUNDED bookingId={}",
                request.getBookingId());

        return buildResponse(transaction);
    }
}