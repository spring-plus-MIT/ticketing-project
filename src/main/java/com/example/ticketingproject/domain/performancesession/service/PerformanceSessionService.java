package com.example.ticketingproject.domain.performancesession.service;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.work.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceSessionService {

    private final PerformanceSessionRepository performanceSessionRepository;

    public Page<GetSessionResponse> getSessions(Long performanceId, Pageable pageable) {
        return performanceSessionRepository.findByPerformanceId(performanceId, pageable)
                .map(GetSessionResponse::from);
    }

    public GetSessionResponse getSessionDetail(Long sessionId) {
        PerformanceSession session = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PerformanceSessionException(SESSION_NOT_FOUND.getHttpStatus(), SESSION_NOT_FOUND));

        return GetSessionResponse.from(session);
    }

    public Page<GetSessionResponse> search(String keyword, Category category, LocalDateTime startTime, LocalDateTime endTime, PerformanceStatus status, Pageable converted) {
        return performanceSessionRepository.searchSessions(keyword, category, startTime, endTime, status, converted);
    }
}