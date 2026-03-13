package com.example.ticketingproject.common.search.dto;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.work.enums.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class PerformanceSearchResponse {

    private final Long performanceId;
    private final String season;
    private final Category category;
    private final PerformanceStatus status;
    private final LocalDate startDate;
    private final LocalDate endDate;
}
