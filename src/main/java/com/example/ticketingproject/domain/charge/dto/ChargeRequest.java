package com.example.ticketingproject.domain.charge.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Getter;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_ERROR;

@Getter
public class ChargeRequest {

    @DecimalMin(value = "0.0", message = MSG_VALIDATION_ERROR)
    @Digits(integer = 8, fraction = 2, message = MSG_VALIDATION_ERROR)
    private BigDecimal amount;
}
