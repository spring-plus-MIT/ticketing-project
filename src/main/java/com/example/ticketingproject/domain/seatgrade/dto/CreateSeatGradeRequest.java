package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
public class CreateSeatGradeRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private GradeName gradeName;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @DecimalMin(value = "0.0", message = MSG_VALIDATION_ERROR)
    @Digits(integer = 8, fraction = 2, message = MSG_VALIDATION_ERROR)
    private BigDecimal price;

    @Min(value = 0, message = MSG_VALIDATION_LENGTH_ERROR)
    private int totalSeats;

    @Min(value = 0, message = MSG_VALIDATION_LENGTH_ERROR)
    private int remainingSeats;
}
