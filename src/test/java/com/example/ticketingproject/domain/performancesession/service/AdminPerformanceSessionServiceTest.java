package com.example.ticketingproject.domain.performancesession.service;

import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.dto.SessionRequest;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminPerformanceSessionServiceTest {

    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private AdminPerformanceSessionService adminPerformanceSessionService;

    private Performance performance;
    private Venue venue;
    private PerformanceSession session;
    private SessionRequest request;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2025, 5, 1, 14, 0);
        endTime = LocalDateTime.of(2025, 5, 1, 17, 0);

        performance = Performance.builder().build();
        ReflectionTestUtils.setField(performance, "id", 1L);

        venue = Venue.builder().build();
        ReflectionTestUtils.setField(venue, "id", 1L);

        session = PerformanceSession.builder()
                .performance(performance)
                .venue(venue)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        ReflectionTestUtils.setField(session, "id", 1L);

        request = new SessionRequest();
        ReflectionTestUtils.setField(request, "venueId", 1L);
        ReflectionTestUtils.setField(request, "startTime", startTime);
        ReflectionTestUtils.setField(request, "endTime", endTime);
    }

    @Test
    @DisplayName("회차 생성 성공 - 겹치는 시간 없음")
    void createSession_success() {
        // given
        given(performanceRepository.findById(1L)).willReturn(Optional.of(performance));
        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));
        given(performanceSessionRepository.existsOverlappingSession(venue, startTime, endTime)).willReturn(false);

        // when
        adminPerformanceSessionService.createSession(1L, request);

        // then
        verify(performanceRepository).findById(1L);
        verify(venueRepository).findById(1L);
        verify(performanceSessionRepository).existsOverlappingSession(venue, startTime, endTime);
        verify(performanceSessionRepository).save(any(PerformanceSession.class));
    }

    @Test
    @DisplayName("회차 생성 실패 - 이미 등록된 시간대와 겹침")
    void createSession_fail_duplicateSession() {
        // given
        given(performanceRepository.findById(1L)).willReturn(Optional.of(performance));
        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));
        given(performanceSessionRepository.existsOverlappingSession(venue, startTime, endTime)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminPerformanceSessionService.createSession(1L, request))
                .isInstanceOf(PerformanceSessionException.class);
    }

    @Test
    @DisplayName("회차 수정 성공 - 자기 자신 제외 겹치는 시간 없음")
    void updateSession_success() {
        // given
        given(performanceSessionRepository.findById(1L)).willReturn(Optional.of(session));
        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));
        given(performanceSessionRepository.existsOverlappingSessionForUpdate(venue, 1L, startTime, endTime)).willReturn(false);

        // when
        adminPerformanceSessionService.updateSession(1L, request);

        // then
        verify(performanceSessionRepository).findById(1L);
        verify(venueRepository).findById(1L);
        verify(performanceSessionRepository).existsOverlappingSessionForUpdate(venue, 1L, startTime, endTime);
        assertThat(session.getStartTime()).isEqualTo(startTime);
    }

    @Test
    @DisplayName("회차 수정 실패 - 다른 회차와 시간대가 겹침")
    void updateSession_fail_duplicateSession() {
        // given
        given(performanceSessionRepository.findById(1L)).willReturn(Optional.of(session));
        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));
        given(performanceSessionRepository.existsOverlappingSessionForUpdate(venue, 1L, startTime, endTime)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminPerformanceSessionService.updateSession(1L, request))
                .isInstanceOf(PerformanceSessionException.class);
    }

    @Test
    @DisplayName("회차 삭제 성공")
    void deleteSession_success() {
        // given
        given(performanceSessionRepository.findById(1L)).willReturn(Optional.of(session));

        // when
        adminPerformanceSessionService.deleteSession(1L);

        // then
        verify(performanceSessionRepository).findById(1L);
    }
}