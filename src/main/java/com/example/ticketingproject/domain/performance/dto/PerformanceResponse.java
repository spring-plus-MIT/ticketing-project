package com.example.ticketingproject.domain.performance.dto;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class PerformanceResponse {
    private Long id;
    private String workTitle;
    private String venueName;
    private String season;
    private LocalDate startDate;
    private LocalDate endDate;
    private PerformanceStatus status;
}