package com.example.ticketingproject.domain.performancesession.dto;

import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
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

    public static GetSessionResponse from(PerformanceSession performanceSession) {
        return GetSessionResponse.builder()
                .id(performanceSession.getId())
                .title(performanceSession.getPerformance().getWork().getTitle())
                .venueName(performanceSession.getVenue().getName())
                .startTime(performanceSession.getStartTime())
                .endTime(performanceSession.getEndTime())
                .build();
    }
}