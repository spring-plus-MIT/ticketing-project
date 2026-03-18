package com.example.ticketingproject.domain.performance.service;

import com.example.ticketingproject.domain.performance.dto.PerformanceRequest;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.exception.PerformanceException;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminPerformanceServiceTest {

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private WorkRepository workRepository;

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private AdminPerformanceService adminPerformanceService;

    private Work work;
    private Venue venue;
    private Performance performance;
    private PerformanceRequest request;

    @BeforeEach
    void setUp() {
        work = Work.builder()
                .title("테스트 뮤지컬")
                .build();
        ReflectionTestUtils.setField(work, "id", 1L);

        venue = Venue.builder()
                .name("테스트 공연장")
                .totalSeats(100)
                .build();
        ReflectionTestUtils.setField(venue, "id", 1L);

        performance = Performance.builder()
                .work(work)
                .venue(venue)
                .season("2025")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 31))
                .status(PerformanceStatus.UPCOMING)
                .build();
        ReflectionTestUtils.setField(performance, "id", 1L);

        request = new PerformanceRequest();

        ReflectionTestUtils.setField(request, "workId", 1L);
        ReflectionTestUtils.setField(request, "venueId", 1L);
        ReflectionTestUtils.setField(request, "season", "2025");
        ReflectionTestUtils.setField(request, "startDate", LocalDate.of(2025, 1, 1));
        ReflectionTestUtils.setField(request, "endDate", LocalDate.of(2025, 1, 31));
        ReflectionTestUtils.setField(request, "status", PerformanceStatus.UPCOMING);
    }

    @Test
    @DisplayName("공연 생성 성공")
    void createPerformance_success() {
        // given
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));
        given(performanceRepository.save(any(Performance.class))).willReturn(performance);

        // when
        adminPerformanceService.createPerformance(request);

        // then
        verify(workRepository).findById(1L);
        verify(venueRepository).findById(1L);
        verify(performanceRepository).save(any(Performance.class));
    }

    @Test
    @DisplayName("공연 생성 실패 - 작품(Work)을 찾을 수 없음")
    void createPerformance_fail_workNotFound() {
        // given
        given(workRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminPerformanceService.createPerformance(request))
                .isInstanceOf(WorkException.class);
    }

    @Test
    @DisplayName("공연 수정 성공")
    void updatePerformance_success() {
        // given
        given(performanceRepository.findById(1L)).willReturn(Optional.of(performance));
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));

        // when
        adminPerformanceService.updatePerformance(1L, request);

        // then
        assertThat(performance.getSeason()).isEqualTo("2025");
        verify(performanceRepository).findById(1L);
    }

    @Test
    @DisplayName("공연 마감(Close) 성공")
    void closePerformance_success() {
        // given
        given(performanceRepository.findById(1L)).willReturn(Optional.of(performance));

        // when
        adminPerformanceService.closePerformance(1L);

        // then
        assertThat(performance.getStatus()).isEqualTo(PerformanceStatus.CLOSED);
        verify(performanceRepository).findById(1L);
    }

    @Test
    @DisplayName("공연 마감 실패 - 공연을 찾을 수 없음")
    void closePerformance_fail_performanceNotFound() {
        // given
        given(performanceRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminPerformanceService.closePerformance(999L))
                .isInstanceOf(PerformanceException.class);
    }
}