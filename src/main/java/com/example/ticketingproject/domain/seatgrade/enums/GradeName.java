package com.example.ticketingproject.domain.seatgrade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GradeName {
    VIP("VIP"),
    R("R"),
    S("S"),
    A("A");

    private final String name;
}