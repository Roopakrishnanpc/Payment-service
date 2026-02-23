package com.movieticket.payment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequestDTO {

    private Long bookingId;
    private String userId;
    private BigDecimal amount;
}