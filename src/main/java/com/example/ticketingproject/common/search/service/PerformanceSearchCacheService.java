package com.example.ticketingproject.common.search.service;

import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.work.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceSearchCacheService {

    private final PerformanceSessionRepository performanceSessionRepository;

    @Cacheable(
            value = "performanceSearch",
            key = "'search:' + (#keyword ?: 'ALL') + ':' + (#category ?: 'ALL') + ':' + (#startDate ?: 'ALL') + ':' + (#endDate ?: 'ALL') + ':' + (#status ?: 'ALL') + ':' + #pageNumber"
    )
    public List<PerformanceSearchResponse> getContent(
            String keyword, Category category,
            LocalDate startDate, LocalDate endDate,
            PerformanceStatus status, Pageable pageable, int pageNumber) {
        return performanceSessionRepository
                .searchPerformance(keyword, category, startDate, endDate, status, pageable)
                .getContent();
    }

    @Cacheable(
            value = "performanceSearch",
            key = "'count:' + (#keyword ?: 'ALL') + ':' + (#category ?: 'ALL') + ':' + (#startDate ?: 'ALL') + ':' + (#endDate ?: 'ALL') + ':' + (#status ?: 'ALL')"
    )
    public long getCount(
            String keyword, Category category,
            LocalDate startDate, LocalDate endDate,
            PerformanceStatus status) {
        return performanceSessionRepository
                .countPerformance(keyword, category, startDate, endDate, status);
    }
}
