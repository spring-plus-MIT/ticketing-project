package com.example.ticketingproject.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;

@Getter
public class CreatePaymentRequest {
    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    private Long reservationId;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    private BigDecimal amount;
}
