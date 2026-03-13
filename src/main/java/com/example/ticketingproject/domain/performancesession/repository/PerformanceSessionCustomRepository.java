package com.example.ticketingproject.domain.performancesession.repository;

import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.work.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PerformanceSessionCustomRepository {

    Page<PerformanceSearchResponse> searchPerformance(
            String keyword,
            Category category,
            LocalDate startTime,
            LocalDate endTime,
            PerformanceStatus status,
            Pageable pageable);
}
