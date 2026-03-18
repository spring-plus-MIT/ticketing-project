package com.example.ticketingproject.common.search.service;

import com.example.ticketingproject.common.config.SuperAdminInitializer;
import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.work.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
class PerformanceSearchCacheServiceIntegrationTest {

    @MockBean
    private SuperAdminInitializer superAdminInitializer;

    @Autowired
    private PerformanceSearchCacheService performanceSearchCacheService;

    @Autowired
    private CacheManager cacheManager;

    @SpyBean
    private PerformanceSessionRepository performanceSessionRepository;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache("performanceSearch")).clear();
    }

    @Test
    @DisplayName("getContent - 캐시 MISS 시 repository를 호출하고 결과를 캐시에 저장한다")
    void getContent_cacheMiss_callsRepository() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PerformanceSearchResponse> page = new PageImpl<>(List.of());

        doReturn(page).when(performanceSessionRepository)
                .searchPerformance(any(), any(), any(), any(), any(), any());

        // when
        performanceSearchCacheService.getContent(
                "레미제라블", null, null, null, null, pageable, 0);

        // then - repository 1회 호출
        then(performanceSessionRepository).should(times(1))
                .searchPerformance(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("getContent - 캐시 HIT 시 repository를 호출하지 않는다")
    void getContent_cacheHit_doesNotCallRepository() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PerformanceSearchResponse> page = new PageImpl<>(List.of());

        doReturn(page).when(performanceSessionRepository)
                .searchPerformance(any(), any(), any(), any(), any(), any());

        // when - 동일한 조건으로 2번 호출
        performanceSearchCacheService.getContent(
                "레미제라블", null, null, null, null, pageable, 0);
        performanceSearchCacheService.getContent(
                "레미제라블", null, null, null, null, pageable, 0);

        // then - repository는 1번만 호출 (두 번째는 캐시 HIT)
        then(performanceSessionRepository).should(times(1))
                .searchPerformance(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("getContent - 검색 조건이 다르면 각각 repository를 호출한다")
    void getContent_differentConditions_callsRepositoryEachTime() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PerformanceSearchResponse> page = new PageImpl<>(List.of());

        doReturn(page).when(performanceSessionRepository)
                .searchPerformance(any(), any(), any(), any(), any(), any());

        // when - 다른 조건으로 2번 호출
        performanceSearchCacheService.getContent(
                "레미제라블", null, null, null, null, pageable, 0);
        performanceSearchCacheService.getContent(
                "오페라의유령", null, null, null, null, pageable, 0);

        // then - 각각 다른 캐시 키라 repository 2번 호출
        then(performanceSessionRepository).should(times(2))
                .searchPerformance(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("getCount - 캐시 MISS 시 repository를 호출하고 결과를 캐시에 저장한다")
    void getCount_cacheMiss_callsRepository() {
        // given
        doReturn(5L).when(performanceSessionRepository)
                .countPerformance(any(), any(), any(), any(), any());

        // when
        long count = performanceSearchCacheService
                .getCount("레미제라블", null, null, null, null);

        // then
        assertThat(count).isEqualTo(5L);
        then(performanceSessionRepository).should(times(1))
                .countPerformance(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("getCount - 캐시 HIT 시 repository를 호출하지 않는다")
    void getCount_cacheHit_doesNotCallRepository() {
        // given
        doReturn(5L).when(performanceSessionRepository)
                .countPerformance(any(), any(), any(), any(), any());

        // when - 동일한 조건으로 2번 호출
        performanceSearchCacheService.getCount(
                "레미제라블", null, null, null, null);
        performanceSearchCacheService.getCount(
                "레미제라블", null, null, null, null);

        // then - repository는 1번만 호출
        then(performanceSessionRepository).should(times(1))
                .countPerformance(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("getCount - 모든 조건으로 검색 시 올바른 파라미터를 전달한다")
    void getCount_withAllConditions_passesCorrectParams() {
        // given
        String keyword = "레미제라블";
        Category category = Category.MUSICAL;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        PerformanceStatus status = PerformanceStatus.ON_SALE;

        doReturn(3L).when(performanceSessionRepository)
                .countPerformance(keyword, category, startDate, endDate, status);

        // when
        long count = performanceSearchCacheService
                .getCount(keyword, category, startDate, endDate, status);

        // then
        assertThat(count).isEqualTo(3L);
        then(performanceSessionRepository).should(times(1))
                .countPerformance(keyword, category, startDate, endDate, status);
    }
}