package com.example.ticketingproject.domain.performance.service;

import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
class AdminPerformanceCacheEvictTest {

    @Autowired
    private AdminPerformanceService adminPerformanceService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private PerformanceRepository performanceRepository;

    @MockBean
    private WorkRepository workRepository;

    @MockBean
    private VenueRepository venueRepository;

    private Cache cache;
    private Performance performance;
    private Work work;
    private Venue venue;

    @BeforeEach
    void setUp() {
        // 캐시 초기화
        cache = Objects.requireNonNull(cacheManager.getCache("performanceSearch"));
        cache.clear();

        // 캐시에 더미 데이터 저장
        cache.put("search:레미제라블:ALL:ALL:ALL:ALL:0", List.of());
        cache.put("search:MUSICAL:ALL:ALL:ALL:ALL:0", List.of());
        cache.put("count:레미제라블:ALL:ALL:ALL:ALL", 5L);

        // Mock 엔티티 세팅
        work = Work.builder().title("테스트 뮤지컬").build();
        ReflectionTestUtils.setField(work, "id", 1L);

        venue = Venue.builder().name("테스트 공연장").totalSeats(100).build();
        ReflectionTestUtils.setField(venue, "id", 1L);

        performance = Performance.builder()
                .work(work).venue(venue)
                .season("2025")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 31))
                .status(PerformanceStatus.UPCOMING)
                .build();
        ReflectionTestUtils.setField(performance, "id", 1L);
    }

    @Test
    @DisplayName("공연 수정 시 performanceSearch 캐시 전체가 삭제된다")
    void updatePerformance_shouldEvictAllSearchCache() {
        // given
        given(performanceRepository.findById(1L)).willReturn(Optional.of(performance));
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));

        com.example.ticketingproject.domain.performance.dto.PerformanceRequest request =
                new com.example.ticketingproject.domain.performance.dto.PerformanceRequest();
        ReflectionTestUtils.setField(request, "workId", 1L);
        ReflectionTestUtils.setField(request, "venueId", 1L);
        ReflectionTestUtils.setField(request, "season", "2025 시즌");
        ReflectionTestUtils.setField(request, "startDate", LocalDate.of(2025, 1, 1));
        ReflectionTestUtils.setField(request, "endDate", LocalDate.of(2025, 6, 30));
        ReflectionTestUtils.setField(request, "status", PerformanceStatus.ON_SALE);

        // evict 전 캐시 존재 확인
        assertThat(cache.get("search:레미제라블:ALL:ALL:ALL:ALL:0")).isNotNull();
        assertThat(cache.get("search:MUSICAL:ALL:ALL:ALL:ALL:0")).isNotNull();
        assertThat(cache.get("count:레미제라블:ALL:ALL:ALL:ALL")).isNotNull();

        // when
        adminPerformanceService.updatePerformance(1L, request);

        // then - 캐시 전체 삭제 확인
        assertThat(cache.get("search:레미제라블:ALL:ALL:ALL:ALL:0")).isNull();
        assertThat(cache.get("search:MUSICAL:ALL:ALL:ALL:ALL:0")).isNull();
        assertThat(cache.get("count:레미제라블:ALL:ALL:ALL:ALL")).isNull();
    }

    @Test
    @DisplayName("공연 종료 시 performanceSearch 캐시 전체가 삭제된다")
    void closePerformance_shouldEvictAllSearchCache() {
        // given
        given(performanceRepository.findById(1L)).willReturn(Optional.of(performance));

        // evict 전 캐시 존재 확인
        assertThat(cache.get("search:레미제라블:ALL:ALL:ALL:ALL:0")).isNotNull();
        assertThat(cache.get("search:MUSICAL:ALL:ALL:ALL:ALL:0")).isNotNull();

        // when
        adminPerformanceService.closePerformance(1L);

        // then - 캐시 전체 삭제 확인
        assertThat(cache.get("search:레미제라블:ALL:ALL:ALL:ALL:0")).isNull();
        assertThat(cache.get("search:MUSICAL:ALL:ALL:ALL:ALL:0")).isNull();
    }
}