package com.example.ticketingproject.domain.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    @NotNull(message = "공연 회차 정보는 필수입니다.")
    private Long performanceSessionId;

    @NotNull(message = "좌석 정보는 필수입니다.")
    private Long seatId;

}