package com.example.ticketingproject.domain.seat.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_LENGTH_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
public class CreateSeatRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Enumerated(EnumType.STRING)
    private GradeName gradeName;

    @Length(min = 1, max = 10, message = "좌석 열 이름은 1자에서 10자 사이여야 합니다.")
    private String rowName;

    @Min(value = 1, message = MSG_VALIDATION_LENGTH_ERROR)
    @Max(value = 10, message = MSG_VALIDATION_LENGTH_ERROR)
    private int seatNumber;
}
