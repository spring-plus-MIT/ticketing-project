package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
public class PutSeatGradeRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Enumerated(EnumType.STRING)
    private GradeName gradeName;
}
