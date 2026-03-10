package com.example.ticketingproject.domain.performancesession.dto;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class SessionRequest {
    private Long venueId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
