package com.example.ticketingproject.domain.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;

@Getter
public class CreatePaymentRequest {
    @NotNull(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(min = 1, max = 255)
    private Long reservationId;

    @NotNull(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @DecimalMin(value = "0.0", message = MSG_VALIDATION_ERROR)
    @Digits(integer = 8, fraction = 0, message = MSG_VALIDATION_ERROR)
    private BigDecimal amount;
}
