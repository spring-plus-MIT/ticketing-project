package com.example.ticketingproject.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    DELETED("DELETED");

    private final String statusName;

}
