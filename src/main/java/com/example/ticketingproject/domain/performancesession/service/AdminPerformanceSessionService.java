package com.example.ticketingproject.domain.performancesession.service;

import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.exception.PerformanceException;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.dto.SessionRequest;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.exception.VenueException;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;
import static com.example.ticketingproject.common.enums.ErrorStatus.DUPLICATE_SESSION;
import static com.example.ticketingproject.common.enums.ErrorStatus.SESSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminPerformanceSessionService {

    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceRepository performanceRepository;
    private final VenueRepository venueRepository;

    public void createSession(Long performanceId, SessionRequest request) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new PerformanceException(PERFORMANCE_NOT_FOUND.getHttpStatus(), PERFORMANCE_NOT_FOUND));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new VenueException(VENUE_NOT_FOUND.getHttpStatus(), VENUE_NOT_FOUND));

        validateOverlappingSession(venue, request.getStartTime(), request.getEndTime());

        performanceSessionRepository.save(PerformanceSession.builder()
                .performance(performance)
                .venue(venue)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build());
    }

    public void updateSession(Long sessionId, SessionRequest request) {
        PerformanceSession session = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PerformanceSessionException(SESSION_NOT_FOUND.getHttpStatus(), SESSION_NOT_FOUND));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new VenueException(VENUE_NOT_FOUND.getHttpStatus(), VENUE_NOT_FOUND));

        validateOverlappingSessionForUpdate(venue, sessionId, request.getStartTime(), request.getEndTime());

        session.update(venue, request.getStartTime(), request.getEndTime());
    }

    public void deleteSession(Long sessionId) {
        PerformanceSession session = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PerformanceSessionException(SESSION_NOT_FOUND.getHttpStatus(), SESSION_NOT_FOUND));

        session.delete();
    }

    private void validateOverlappingSession(Venue venue, LocalDateTime startTime, LocalDateTime endTime) {
        if (performanceSessionRepository.existsOverlappingSession(venue, startTime, endTime)) {
            throw new PerformanceSessionException(DUPLICATE_SESSION.getHttpStatus(), DUPLICATE_SESSION);
        }
    }

    private void validateOverlappingSessionForUpdate(Venue venue, Long sessionId, LocalDateTime startTime, LocalDateTime endTime) {
        if (performanceSessionRepository.existsOverlappingSessionForUpdate(venue, sessionId, startTime, endTime)) {
            throw new PerformanceSessionException(DUPLICATE_SESSION.getHttpStatus(), DUPLICATE_SESSION);
        }
    }
}
