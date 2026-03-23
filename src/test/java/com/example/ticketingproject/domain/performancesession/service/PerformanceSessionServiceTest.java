package com.example.ticketingproject.domain.performancesession.service;

import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.common.search.service.PerformanceSearchCacheService;
import com.example.ticketingproject.common.search.service.SearchRankingService;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PerformanceSessionServiceTest {

    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    @Mock
    private SearchRankingService searchRankingService;

    @Mock
    private PerformanceSearchCacheService performanceSearchCacheService;

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
    @DisplayName("공연 검색 시 인기 검색어 기록 호출 확인")
    void search_recordsKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PerformanceSearchResponse> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        given(performanceSessionRepository.searchPerformance(
                "레미제라블", null, null, null, null, pageable
        )).willReturn(emptyPage);

        performanceSessionService.search("레미제라블", null, null, null, null, pageable, 1L);

        then(searchRankingService).should().recordKeyword("레미제라블", 1L, "performance");
    }

    @Test
    @DisplayName("공연 검색 v1 성공 - 키워드 검색")
    void search_success_withKeyword() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        PerformanceSearchResponse dto = new PerformanceSearchResponse(
                1L, "2025 시즌", Category.MUSICAL, PerformanceStatus.ON_SALE
        );
        Page<PerformanceSearchResponse> searchPage = new PageImpl<>(List.of(dto), pageable, 1);

        given(performanceSessionRepository.searchPerformance(
                "레미제라블", null, null, null, null, pageable
        )).willReturn(searchPage);

        // when
        Page<PerformanceSearchResponse> response = performanceSessionService.search(
                "레미제라블", null, null, null, null, pageable, 1L
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getPerformanceId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getCategory()).isEqualTo(Category.MUSICAL);
    }

    @Test
    @DisplayName("공연 검색 v1 성공 - 카테고리 필터")
    void search_success_withCategory() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        PerformanceSearchResponse dto = new PerformanceSearchResponse(
                1L, "2025 시즌", Category.MUSICAL, PerformanceStatus.ON_SALE
        );
        Page<PerformanceSearchResponse> searchPage = new PageImpl<>(List.of(dto), pageable, 1);

        given(performanceSessionRepository.searchPerformance(
                null, Category.MUSICAL, null, null, null, pageable
        )).willReturn(searchPage);

        // when
        Page<PerformanceSearchResponse> response = performanceSessionService.search(
                null, Category.MUSICAL, null, null, null, pageable, 1L
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getCategory()).isEqualTo(Category.MUSICAL);
    }

    @Test
    @DisplayName("공연 검색 v1 성공 - 날짜 범위 필터")
    void search_success_withDateRange() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 31);
        PerformanceSearchResponse dto = new PerformanceSearchResponse(
                1L, "2025 시즌", Category.MUSICAL, PerformanceStatus.ON_SALE
        );
        Page<PerformanceSearchResponse> searchPage = new PageImpl<>(List.of(dto), pageable, 1);

        given(performanceSessionRepository.searchPerformance(
                null, null, startDate, endDate, null, pageable
        )).willReturn(searchPage);

        // when
        Page<PerformanceSearchResponse> response = performanceSessionService.search(
                null, null, startDate, endDate, null, pageable, 1L
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("공연 검색 v1 성공 - 상태 필터")
    void search_success_withStatus() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        PerformanceSearchResponse dto = new PerformanceSearchResponse(
                1L, "2025 시즌", Category.MUSICAL, PerformanceStatus.ON_SALE
        );
        Page<PerformanceSearchResponse> searchPage = new PageImpl<>(List.of(dto), pageable, 1);

        given(performanceSessionRepository.searchPerformance(
                null, null, null, null, PerformanceStatus.ON_SALE, pageable
        )).willReturn(searchPage);

        // when
        Page<PerformanceSearchResponse> response = performanceSessionService.search(
                null, null, null, null, PerformanceStatus.ON_SALE, pageable, 1L
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getStatus()).isEqualTo(PerformanceStatus.ON_SALE);
    }

    @Test
    @DisplayName("공연 검색 v1 성공 - 검색 결과 없음")
    void search_success_emptyResult() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PerformanceSearchResponse> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        given(performanceSessionRepository.searchPerformance(
                "존재하지않는작품", null, null, null, null, pageable
        )).willReturn(emptyPage);

        // when
        Page<PerformanceSearchResponse> response = performanceSessionService.search(
                "존재하지않는작품", null, null, null, null, pageable, 1L
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(0);
        assertThat(response.getContent()).isEmpty();
    }

    @Test
    @DisplayName("공연 검색 v2 성공 - 캐시 서비스 호출 확인")
    void searchV2_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        PerformanceSearchResponse dto = new PerformanceSearchResponse(
                1L, "2025 시즌", Category.MUSICAL, PerformanceStatus.ON_SALE
        );

        given(performanceSearchCacheService.getContent(
                "레미제라블", null, null, null, null, pageable, 0
        )).willReturn(List.of(dto));

        given(performanceSearchCacheService.getCount(
                "레미제라블", null, null, null, null
        )).willReturn(1L);

        // when
        Page<PerformanceSearchResponse> response = performanceSessionService.searchV2(
                "레미제라블", null, null, null, null, pageable, 1L
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getPerformanceId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("공연 검색 v2 시 인기 검색어 기록 호출 확인")
    void searchV2_recordsKeyword() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        given(performanceSearchCacheService.getContent(
                "레미제라블", null, null, null, null, pageable, 0
        )).willReturn(List.of());

        given(performanceSearchCacheService.getCount(
                "레미제라블", null, null, null, null
        )).willReturn(0L);

        // when
        performanceSessionService.searchV2("레미제라블", null, null, null, null, pageable, 1L);

        // then
        then(searchRankingService).should().recordKeyword("레미제라블", 1L, "performance");
    }

    @Test
    @DisplayName("공연 검색 v2 성공 - 검색 결과 없음")
    void searchV2_emptyResult() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        given(performanceSearchCacheService.getContent(
                "존재하지않는작품", null, null, null, null, pageable, 0
        )).willReturn(List.of());

        given(performanceSearchCacheService.getCount(
                "존재하지않는작품", null, null, null, null
        )).willReturn(0L);

        // when
        Page<PerformanceSearchResponse> response = performanceSessionService.searchV2(
                "존재하지않는작품", null, null, null, null, pageable, 1L
        );

        // then
        assertThat(response.getTotalElements()).isEqualTo(0);
        assertThat(response.getContent()).isEmpty();
    }
}