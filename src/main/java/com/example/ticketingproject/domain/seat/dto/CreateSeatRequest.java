package com.example.ticketingproject.domain.seat.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
public class CreateSeatRequest {

    @NotBlank(message = MSG_VALIDATION_ERROR)
    @Enumerated(EnumType.STRING)
    private GradeName gradeName;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(min = 1, max = 10, message = MSG_VALIDATION_LENGTH_ERROR)
    private String rowName;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Min(value = 1, message = MSG_VALIDATION_LENGTH_ERROR)
    @Max(value = 10, message = MSG_VALIDATION_LENGTH_ERROR)
    private int seatNumber;
}
