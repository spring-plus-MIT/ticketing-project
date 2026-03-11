package com.example.ticketingproject.domain.seat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeatStatus {
    AVAILABLE("AVAILABLE"),
    RESERVED("RESERVED"),
    SOLD("SOLD");

    private final String seatStatusName;
}
