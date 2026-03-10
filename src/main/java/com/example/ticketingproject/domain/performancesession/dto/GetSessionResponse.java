package com.example.ticketingproject.domain.performancesession.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetSessionResponse {
    private final Long id;
    private final String title;
    private final String venueName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
}