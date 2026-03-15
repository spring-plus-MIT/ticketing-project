package com.example.ticketingproject.domain.performancesession.service;

import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.common.search.service.SearchRankingService;
import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.work.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.example.ticketingproject.common.enums.ErrorStatus.SESSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceSessionService {

    private final PerformanceSessionRepository performanceSessionRepository;
    private final SearchRankingService searchRankingService;

    public Page<GetSessionResponse> getSessions(Long performanceId, Pageable pageable) {
        return performanceSessionRepository.findByPerformanceId(performanceId, pageable)
                .map(GetSessionResponse::from);
    }

    public GetSessionResponse getSessionDetail(Long sessionId) {
        PerformanceSession session = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PerformanceSessionException(SESSION_NOT_FOUND.getHttpStatus(), SESSION_NOT_FOUND));

        return GetSessionResponse.from(session);
    }

    public Page<PerformanceSearchResponse> search(String keyword, Category category, LocalDate startDate, LocalDate endDate, PerformanceStatus status, Pageable converted, Long userId) {
        searchRankingService.recordKeyword(keyword, userId, "performance");
        return performanceSessionRepository.searchPerformance(keyword, category, startDate, endDate, status, converted);
    }

    @Cacheable(
            value = "performanceSearch",
            key = "'search:' + (#keyword ?: 'ALL') + ':' + (#category ?: 'ALL') + ':' + (#startDate ?: 'ALL') + ':' + (#endDate ?: 'ALL') + ':' + (#status ?: 'ALL') + ':' + #converted.pageNumber"
    )
    public Page<PerformanceSearchResponse> searchV2(String keyword, Category category, LocalDate startDate, LocalDate endDate, PerformanceStatus status, Pageable converted, Long userId) {
        searchRankingService.recordKeyword(keyword, userId, "performance");
        return performanceSessionRepository.searchPerformance(keyword, category, startDate, endDate, status, converted);
    }
}