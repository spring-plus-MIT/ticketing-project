package com.example.ticketingproject.domain.reservation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {

    PENDING("결제 대기"),
    CONFIRMED("예약 확정"),
    CANCELED("예약 취소");

    private final String description; // statusName 대신 description이나 한글명으로 관리

}