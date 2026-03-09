package com.example.ticketingproject.domain.performance.dto;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PerformanceRequest {
    private Long workId;
    private Long venueId;
    private String season;
    private LocalDate startDate;
    private LocalDate endDate;
    private PerformanceStatus status;
}