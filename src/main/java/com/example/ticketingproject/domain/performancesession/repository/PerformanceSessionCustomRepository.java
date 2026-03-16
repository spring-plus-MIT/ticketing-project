package com.example.ticketingproject.domain.performancesession.repository;

import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.work.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PerformanceSessionCustomRepository {

    Page<PerformanceSearchResponse> searchPerformance(
            String keyword,
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            PerformanceStatus status,
            Pageable pageable);

    long countPerformance(
            String keyword,
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            PerformanceStatus status);
}
