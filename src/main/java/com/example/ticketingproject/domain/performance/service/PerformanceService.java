package com.example.ticketingproject.domain.performance.service;

import com.example.ticketingproject.domain.performance.dto.PerformanceResponse;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.exception.PerformanceException;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    public Page<PerformanceResponse> getPerformances(Pageable pageable) {
        return performanceRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    public PerformanceResponse getPerformanceDetail(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new PerformanceException(PERFORMANCE_NOT_FOUND.getHttpStatus(), PERFORMANCE_NOT_FOUND));
        return convertToResponse(performance);
    }

    private PerformanceResponse convertToResponse(Performance p) {
        return PerformanceResponse.builder()
                .id(p.getId())
                .workTitle(p.getWork().getTitle())
                .venueName(p.getVenue().getName())
                .season(p.getSeason())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus())
                .build();
    }
}