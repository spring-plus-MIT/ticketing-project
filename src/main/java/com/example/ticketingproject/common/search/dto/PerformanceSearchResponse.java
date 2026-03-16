package com.example.ticketingproject.common.search.dto;

import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.work.enums.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PerformanceSearchResponse {

    private final Long performanceId;
    private final String season;
    private final Category category;
    private final PerformanceStatus status;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
}
