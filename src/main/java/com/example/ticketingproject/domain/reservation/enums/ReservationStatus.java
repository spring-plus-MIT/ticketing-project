package com.example.ticketingproject.domain.reservation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {

    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELED("CANCELED");

    private final String statusName;

}
