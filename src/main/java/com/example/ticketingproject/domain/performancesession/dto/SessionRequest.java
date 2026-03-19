package com.example.ticketingproject.domain.performancesession.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
public class SessionRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private Long venueId;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private LocalDateTime startTime;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private LocalDateTime endTime;
}