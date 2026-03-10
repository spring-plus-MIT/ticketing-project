package com.example.ticketingproject.domain.reservation.dto.request;

import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class AdminReservationUpdateDto {
    @NotNull(message = "변경할 상태값은 필수입니다.")
    private ReservationStatus status;
}