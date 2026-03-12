package com.example.ticketingproject.domain.performancesession.service;

import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PerformanceSessionServiceTest {

    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    @InjectMocks
    private PerformanceSessionService performanceSessionService;

    private PerformanceSession session;

    @BeforeEach
    void setUp() {
        Work work = Work.builder().title("테스트 작품").build();

        Performance performance = Performance.builder().work(work).build();
        ReflectionTestUtils.setField(performance, "id", 1L);

        Venue venue = Venue.builder().name("테스트 공연장").build();
        ReflectionTestUtils.setField(venue, "id", 1L);

        session = PerformanceSession.builder()
                .performance(performance)
                .venue(venue)
                .startTime(LocalDateTime.of(2025, 5, 1, 14, 0))
                .endTime(LocalDateTime.of(2025, 5, 1, 17, 0))
                .build();
        ReflectionTestUtils.setField(session, "id", 1L);
    }

    @Test
    @DisplayName("회차 목록 조회 성공")
    void getSessions_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PerformanceSession> sessionPage = new PageImpl<>(List.of(session), pageable, 1);
        given(performanceSessionRepository.findByPerformanceId(1L, pageable)).willReturn(sessionPage);

        // when
        Page<GetSessionResponse> response = performanceSessionService.getSessions(1L, pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("테스트 작품");
        assertThat(response.getContent().get(0).getVenueName()).isEqualTo("테스트 공연장");
    }

    @Test
    @DisplayName("회차 단건 조회 성공")
    void getSessionDetail_success() {
        // given
        given(performanceSessionRepository.findById(1L)).willReturn(Optional.of(session));

        // when
        GetSessionResponse response = performanceSessionService.getSessionDetail(1L);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("테스트 작품");
        assertThat(response.getStartTime()).isEqualTo(LocalDateTime.of(2025, 5, 1, 14, 0));
    }

    @Test
    @DisplayName("회차 단건 조회 실패 - 존재하지 않는 회차")
    void getSessionDetail_fail_notFound() {
        // given
        given(performanceSessionRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> performanceSessionService.getSessionDetail(999L))
                .isInstanceOf(PerformanceSessionException.class);
    }

    @Test
    @DisplayName("공연 세션 검색 성공 - 키워드 포함 결과 반환")
    void search_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        GetSessionResponse dto = GetSessionResponse.builder()
                .id(1L)
                .title("테스트 작품")
                .venueName("테스트 공연장")
                .startTime(LocalDateTime.of(2025, 5, 1, 14, 0))
                .endTime(LocalDateTime.of(2025, 5, 1, 17, 0))
                .build();

        Page<GetSessionResponse> sessionPage = new PageImpl<>(List.of(dto), pageable, 1);

        given(performanceSessionRepository.searchSessions(
                "테스트 작품", null, null, null, null, pageable
        )).willReturn(sessionPage);

        // when
        Page<GetSessionResponse> response = performanceSessionService.search(
                "테스트 작품", null, null, null, null, pageable
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("테스트 작품");
        assertThat(response.getContent().get(0).getVenueName()).isEqualTo("테스트 공연장");
    }

    @Test
    @DisplayName("공연 세션 검색 성공 - 카테고리 필터 적용")
    void search_success_withCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        GetSessionResponse dto = GetSessionResponse.builder()
                .id(1L)
                .title("테스트 작품")
                .venueName("테스트 공연장")
                .startTime(LocalDateTime.of(2025, 5, 1, 14, 0))
                .endTime(LocalDateTime.of(2025, 5, 1, 17, 0))
                .build();

        Page<GetSessionResponse> sessionPage = new PageImpl<>(List.of(dto), pageable, 1);

        given(performanceSessionRepository.searchSessions(
                null, Category.MUSICAL, null, null, null, pageable
        )).willReturn(sessionPage);

        // when
        Page<GetSessionResponse> response = performanceSessionService.search(
                null, Category.MUSICAL, null, null, null, pageable
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("테스트 작품");
    }

    @Test
    @DisplayName("공연 세션 검색 성공 - 날짜 범위 필터 적용")
    void search_success_withDateRange() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 5, 31, 23, 59);

        GetSessionResponse dto = GetSessionResponse.builder()
                .id(1L)
                .title("테스트 작품")
                .venueName("테스트 공연장")
                .startTime(LocalDateTime.of(2025, 5, 1, 14, 0))
                .endTime(LocalDateTime.of(2025, 5, 1, 17, 0))
                .build();

        Page<GetSessionResponse> sessionPage = new PageImpl<>(List.of(dto), pageable, 1);

        given(performanceSessionRepository.searchSessions(
                null, null, startTime, endTime, null, pageable
        )).willReturn(sessionPage);

        // when
        Page<GetSessionResponse> response = performanceSessionService.search(
                null, null, startTime, endTime, null, pageable
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getStartTime())
                .isAfterOrEqualTo(startTime);
        assertThat(response.getContent().get(0).getEndTime())
                .isBeforeOrEqualTo(endTime);
    }

    @Test
    @DisplayName("공연 세션 검색 성공 - 상태 필터 적용")
    void search_success_withStatus() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        GetSessionResponse dto = GetSessionResponse.builder()
                .id(1L)
                .title("테스트 작품")
                .venueName("테스트 공연장")
                .startTime(LocalDateTime.of(2025, 5, 1, 14, 0))
                .endTime(LocalDateTime.of(2025, 5, 1, 17, 0))
                .build();

        Page<GetSessionResponse> sessionPage = new PageImpl<>(List.of(dto), pageable, 1);

        given(performanceSessionRepository.searchSessions(
                null, null, null, null, PerformanceStatus.ON_SALE, pageable
        )).willReturn(sessionPage);

        // when
        Page<GetSessionResponse> response = performanceSessionService.search(
                null, null, null, null, PerformanceStatus.ON_SALE, pageable
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("공연 세션 검색 성공 - 검색 결과 없음")
    void search_success_emptyResult() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<GetSessionResponse> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        given(performanceSessionRepository.searchSessions(
                "존재하지않는작품", null, null, null, null, pageable
        )).willReturn(emptyPage);

        // when
        Page<GetSessionResponse> response = performanceSessionService.search(
                "존재하지않는작품", null, null, null, null, pageable
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(0);
        assertThat(response.getContent()).isEmpty();
    }
}