package com.example.ticketingproject.domain.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    CANCELED("CANCELED");

    private final String PaymentStatusName;
}
