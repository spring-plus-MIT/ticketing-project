package com.example.ticketingproject.common.search.dto;

import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.work.enums.Category;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PerformanceSearchResponse {

    private final Long performanceId;
    private final String season;
    private final Category category;
    private final PerformanceStatus status;

    @JsonCreator
    public PerformanceSearchResponse(
            @JsonProperty("performanceId") Long performanceId,
            @JsonProperty("season") String season,
            @JsonProperty("category") Category category,
            @JsonProperty("status") PerformanceStatus status
    ) {
        this.performanceId = performanceId;
        this.season = season;
        this.category = category;
        this.status = status;
    }
}
