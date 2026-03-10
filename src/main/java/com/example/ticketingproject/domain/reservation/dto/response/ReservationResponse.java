package com.example.ticketingproject.domain.reservation.dto.response;

import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ReservationResponse {

    private Long id;
    private Long userId;
    private String performanceTitle;
    private LocalDate performanceDate;
    private String seatInfo;
    private BigDecimal totalPrice;
    private ReservationStatus status;

    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .performanceTitle(reservation.getPerformance().getWork().getTitle())
                .performanceDate(reservation.getPerformance().getStartDate()) // LocalDate로 맞춤
                .seatInfo(reservation.getSeat().getGradeName().name() + " - " + reservation.getSeat().getSeatNumber())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .build();
    }
}
