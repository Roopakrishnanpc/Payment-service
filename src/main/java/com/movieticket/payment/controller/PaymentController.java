package com.movieticket.payment.controller;

import com.movieticket.payment.dto.*;
import com.movieticket.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/charge")
    public PaymentResponseDTO charge(
            @RequestBody PaymentRequestDTO request) {

        log.info("HTTP_REQUEST_CHARGE bookingId={}",
                request.getBookingId());

        PaymentResponseDTO response =
                paymentService.charge(request);

        log.info("HTTP_RESPONSE_CHARGE bookingId={} status={}",
                response.getBookingId(),
                response.getStatus());

        return response;
    }

    @PostMapping("/refund")
    public PaymentResponseDTO refund(
            @RequestBody PaymentRequestDTO request) {

        log.info("HTTP_REQUEST_REFUND bookingId={}",
                request.getBookingId());

        PaymentResponseDTO response =
                paymentService.refund(request);

        log.info("HTTP_RESPONSE_REFUND bookingId={} status={}",
                response.getBookingId(),
                response.getStatus());

        return response;
    }
}