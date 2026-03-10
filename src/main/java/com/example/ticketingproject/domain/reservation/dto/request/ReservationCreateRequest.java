package com.example.ticketingproject.domain.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ReservationCreateRequest {

    @NotNull(message = "공연 회차 ID는 필수입니다.")
    private Long performanceSessionId;

    @NotNull(message = "좌석 ID는 필수입니다.")
    private Long seatId;
}