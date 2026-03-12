package com.example.ticketingproject.domain.performancesession.repository;

import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.work.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PerformanceSessionCustomRepository {

    Page<GetSessionResponse> searchSessions(
            String keyword,
            Category category,
            LocalDateTime startTime,
            LocalDateTime endTime,
            PerformanceStatus status,
            Pageable pageable);
}
