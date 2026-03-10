package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_ERROR;

@Getter
public class CreateSeatGradeRequest {

    @NotBlank(message = MSG_VALIDATION_ERROR)
    private GradeName gradeName;

    @NotBlank(message = MSG_VALIDATION_ERROR)
    @DecimalMin(value = "0.0", message = MSG_VALIDATION_ERROR)
    @Digits(integer = 8, fraction = 2, message = MSG_VALIDATION_ERROR)
    private BigDecimal price;

    @NotBlank
    private int totalSeats;

    @NotBlank
    private int remainingSeats;
}
