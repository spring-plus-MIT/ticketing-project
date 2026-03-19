package com.example.ticketingproject.domain.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
public class ReservationCreateRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private Long performanceSessionId;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private Long seatId;

}
