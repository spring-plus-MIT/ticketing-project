package com.example.ticketingproject.domain.payment.dto;

import com.example.ticketingproject.domain.payment.entity.Payment;
import com.example.ticketingproject.domain.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentResponse {
    private final Long id;
    private final Long reservationId;
    private final Long userId;
    private final BigDecimal amount;
    private final PaymentStatus paymentStatus;
    private final BigDecimal balanceAfterCharge;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .reservationId(payment.getReservation().getId())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .balanceAfterCharge(payment.getBalanceAfterCharge())
                .build();
    }

}
