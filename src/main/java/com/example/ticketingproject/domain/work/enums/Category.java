package com.example.ticketingproject.domain.work.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    MUSICAL("MUSICAL"),
    CONCERT("CONCERT"),
    PLAY("PLAY"),
    CLASSIC("CLASSIC"),
    DANCE("DANCE"),
    ETC("ETC");

    private final String categoryName;
}
