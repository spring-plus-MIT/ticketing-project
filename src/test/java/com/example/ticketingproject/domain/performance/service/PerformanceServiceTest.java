package com.example.ticketingproject.domain.performance.service;

import com.example.ticketingproject.domain.performance.dto.PerformanceResponse;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.performance.exception.PerformanceException;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.work.entity.Work;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @Mock
    private PerformanceRepository performanceRepository;

    @InjectMocks
    private PerformanceService performanceService;

    private Performance performance;

    @BeforeEach
    void setUp() {
        Work work = Work.builder().title("테스트 뮤지컬").build();
        Venue venue = Venue.builder().name("테스트 공연장").build();

        performance = Performance.builder()
                .work(work)
                .venue(venue)
                .season("2025")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 31))
                .status(PerformanceStatus.UPCOMING)
                .build();
        ReflectionTestUtils.setField(performance, "id", 1L);
    }

    @Test
    @DisplayName("공연 목록 조회 성공")
    void getPerformances_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Performance> performancePage = new PageImpl<>(List.of(performance), pageable, 1);
        given(performanceRepository.findAll(pageable)).willReturn(performancePage);

        // when
        Page<PerformanceResponse> response = performanceService.getPerformances(pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getWorkTitle()).isEqualTo("테스트 뮤지컬");
        assertThat(response.getContent().get(0).getVenueName()).isEqualTo("테스트 공연장");
    }

    @Test
    @DisplayName("공연 단건 조회 성공")
    void getPerformanceDetail_success() {
        // given
        given(performanceRepository.findById(1L)).willReturn(Optional.of(performance));

        // when
        PerformanceResponse response = performanceService.getPerformanceDetail(1L);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getWorkTitle()).isEqualTo("테스트 뮤지컬");
        assertThat(response.getSeason()).isEqualTo("2025");
        assertThat(response.getStatus()).isEqualTo(PerformanceStatus.UPCOMING);
    }

    @Test
    @DisplayName("공연 단건 조회 실패 - 공연을 찾을 수 없음")
    void getPerformanceDetail_fail_notFound() {
        // given
        given(performanceRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> performanceService.getPerformanceDetail(999L))
                .isInstanceOf(PerformanceException.class);
    }
}