package com.movieticket.payment.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Long bookingId;
    private Long transactionId;
    private String status;
    private boolean success;
    
}
