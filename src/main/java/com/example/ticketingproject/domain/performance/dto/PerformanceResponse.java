package com.example.ticketingproject.domain.performance.dto;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PerformanceResponse {
    private final Long id;
    private final String workTitle;
    private final String venueName;
    private final String season;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final PerformanceStatus status;
}