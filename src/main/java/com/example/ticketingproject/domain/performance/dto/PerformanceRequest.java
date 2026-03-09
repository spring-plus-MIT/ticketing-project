package com.example.ticketingproject.domain.performance.dto;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PerformanceRequest {
    private Long workId;
    private Long venueId;
    private String season;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PerformanceStatus status;
}