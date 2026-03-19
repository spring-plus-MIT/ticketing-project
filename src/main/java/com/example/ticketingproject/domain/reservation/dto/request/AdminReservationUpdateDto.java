package com.example.ticketingproject.domain.reservation.dto.request;

import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
public class AdminReservationUpdateDto {
    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private ReservationStatus status;
}