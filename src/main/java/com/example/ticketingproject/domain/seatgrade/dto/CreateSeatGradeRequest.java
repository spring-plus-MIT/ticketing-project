package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
public class CreateSeatGradeRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Enumerated(EnumType.STRING)
    private GradeName gradeName;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @DecimalMin(value = "0.0", message = MSG_VALIDATION_DECIMAL_MIN_ERROR)
    @Digits(integer = 8, fraction = 2, message = MSG_VALIDATION_DIGITS_ERROR)
    private BigDecimal price;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Min(value = 1, message = "전체 좌석 수는 1개 이상으로 입력해주세요")
    private int totalSeats;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Min(value = 0, message = "남은 좌석 수는 0개 이상으로 입력해주세요")
    private int remainingSeats;
}
