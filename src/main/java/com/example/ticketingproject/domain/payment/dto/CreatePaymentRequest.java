package com.example.ticketingproject.domain.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
public class CreatePaymentRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private Long reservationId;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @DecimalMin(value = "0.0", message = MSG_VALIDATION_DECIMAL_MIN_ERROR)
    @Digits(integer = 8, fraction = 0, message = MSG_VALIDATION_DIGITS_ERROR)
    private BigDecimal amount;
}