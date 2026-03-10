package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_ERROR;

@Getter
@Builder
public class PutSeatGradeRequest {

    @NotBlank(message = MSG_VALIDATION_ERROR)
    private GradeName gradeName;
}
