package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreateSeatGradeRequest {

    @NotBlank(message = "")
    private GradeName gradeName;

    @NotBlank(message = "")
    @DecimalMin(value = "0.0", inclusive = true, message = "가격은 0 이상이어야 합니다.")
    @Digits(integer = 8, fraction = 2, message = "가격은 정수 8자리, 소수 2자리까지 허용됩니다.")
    private BigDecimal price;

    @NotBlank
    private int totalSeats;

    @NotBlank
    private int remainingSeats;
}
