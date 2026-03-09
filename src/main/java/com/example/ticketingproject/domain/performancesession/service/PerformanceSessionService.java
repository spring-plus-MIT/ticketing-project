package com.example.ticketingproject.domain.performancesession.service;

import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.dto.SessionRequest;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceSessionService {

    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceRepository performanceRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public void createSession(Long performanceId, SessionRequest request) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("공연 정보를 찾을 수 없습니다."));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("장소 정보를 찾을 수 없습니다."));

        validateDuplicateSession(venue, request.getSessionDateTime());

        performanceSessionRepository.save(PerformanceSession.builder()
                .performance(performance)
                .venue(venue)
                .sessionDateTime(request.getSessionDateTime())
                .build());
    }

    public Page<GetSessionResponse> getSessions(Long performanceId, Pageable pageable) {
        return performanceSessionRepository.findByPerformanceId(performanceId, pageable)
                .map(this::convertToResponse);
    }

    public GetSessionResponse getSessionDetail(Long sessionId) {
        PerformanceSession session = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회차를 찾을 수 없습니다."));

        return convertToResponse(session);
    }

    @Transactional
    public void updateSession(Long sessionId, SessionRequest request) {
        PerformanceSession session = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회차를 찾을 수 없습니다."));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("장소 정보를 찾을 수 없습니다."));

        validateDuplicateSession(venue, request.getSessionDateTime());

        session.update(venue, request.getSessionDateTime());
    }

    private void validateDuplicateSession(Venue venue, LocalDateTime datetime) {
        if (performanceSessionRepository.existsByVenueAndSessionDateAndSessionTime(venue, datetime)) {
            throw new IllegalStateException("해당 장소와 시간에 이미 등록된 공연 회차가 있습니다.");
        }
    }

    private GetSessionResponse convertToResponse(PerformanceSession s) {
        return new GetSessionResponse(
                s.getId(),
                s.getPerformance().getWork().getTitle(),
                s.getVenue().getName(),
                s.getSessionDateTime()
        );
    }
}