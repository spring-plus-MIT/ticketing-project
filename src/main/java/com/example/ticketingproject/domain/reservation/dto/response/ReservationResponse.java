package com.example.ticketingproject.domain.reservation.dto.response;

import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponse {

    private Long id;
    private Long userId;
    private String performanceTitle;
    private LocalDate performanceDate;
    private LocalDateTime startTime;
    private String seatInfo;
    private BigDecimal totalPrice;
    private ReservationStatus status;


    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .performanceTitle(reservation.getPerformanceSession().getPerformance().getWork().getTitle())
                .performanceDate(reservation.getPerformanceSession().getPerformance().getStartDate())
                .startTime(reservation.getPerformanceSession().getStartTime())
                .seatInfo(reservation.getSeat().getSeatGrade().getGradeName().name() + " - " + reservation.getSeat().getSeatNumber())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .build();
    }
}
