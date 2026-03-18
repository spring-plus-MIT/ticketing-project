package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_ERROR;

@Getter
public class PutSeatGradeRequest {

    @NotBlank(message = MSG_VALIDATION_ERROR)
    private GradeName gradeName;
}
