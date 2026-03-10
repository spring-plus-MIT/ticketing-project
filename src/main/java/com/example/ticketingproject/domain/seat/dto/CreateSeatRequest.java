package com.example.ticketingproject.domain.seat.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_ERROR;

@Getter
@Builder
public class CreateSeatRequest {

    @NotBlank(message = MSG_VALIDATION_ERROR)
    @Enumerated(EnumType.STRING)
    private GradeName gradeName;

    @NotBlank(message = MSG_VALIDATION_ERROR)
    @Length(min = 1, max = 10, message = MSG_VALIDATION_ERROR)
    private String rowName;

    @NotBlank(message = MSG_VALIDATION_ERROR)
    @Length(min = 1, max = 10, message = MSG_VALIDATION_ERROR)
    private int seatNumber;
}
