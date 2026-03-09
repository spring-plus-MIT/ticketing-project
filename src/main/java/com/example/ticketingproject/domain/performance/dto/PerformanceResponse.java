package com.example.ticketingproject.domain.performance.dto;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PerformanceResponse {
    private Long id;
    private String workTitle;
    private String venueName;
    private String season;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PerformanceStatus status;
}