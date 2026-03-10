package com.example.ticketingproject.domain.reservation.dto.response;

import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performance.entity.Performance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {

    private Long id;
    private String workTitle;         // 공연 제목
    private LocalDateTime startTime;  // 공연 시작 시간
    private String seatNumber;        // 좌석 번호
    private BigDecimal totalPrice;    // 결제 금액
    private ReservationStatus status; // 상태
    private LocalDateTime reservedAt; // 예약 시간
    private LocalDateTime expiresAt;  // 만료 시간

    public static ReservationResponseDto from(Reservation reservation) {

        PerformanceSession session = reservation.getPerformanceSession();
        Performance performance = session.getPerformance();

        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .workTitle(performance.getWork().getTitle())
                .startTime(session.getStartTime())
                .seatNumber(String.valueOf(reservation.getSeat().getSeatNumber()))
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .reservedAt(reservation.getReservedAt())
                .expiresAt(reservation.getExpiresAt())
                .build();
    }
}
