package com.example.ticketingproject.domain.seatgrade.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.seatgrade.dto.SeatGradeResponse;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.exeption.SeatGradeException;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SeatGradeServiceTest {

    @Mock
    private SeatGradeRepository seatGradeRepository;

    @InjectMocks
    private SeatGradeService seatGradeService;

    private SeatGrade seatGrade;
    private SeatGrade seatGrade2;
    private PerformanceSession performanceSession;
    private Performance performance;
    private Venue venue;

    @BeforeEach
    void setUp() {
        performanceSession = PerformanceSession.builder()
                .performance(performance)
                .venue(venue)
                .build();
        ReflectionTestUtils.setField(performanceSession, "id", 1L);

        seatGrade = SeatGrade.builder()
                .performanceSession(performanceSession)
                .gradeName(GradeName.VIP)
                .price(new BigDecimal(10000))
                .totalSeats(100)
                .remainingSeats(50)
                .build();
        ReflectionTestUtils.setField(seatGrade, "id", 1L);

        seatGrade2 = SeatGrade.builder()
                .performanceSession(performanceSession)
                .gradeName(GradeName.R)
                .price(new BigDecimal(8000))
                .totalSeats(200)
                .remainingSeats(150)
                .build();
        ReflectionTestUtils.setField(seatGrade2, "id", 2L);
    }

    @Test
    @DisplayName("좌석 등급 전체 조회 성공")
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SeatGrade> seatGradePage = new PageImpl<>(List.of(seatGrade, seatGrade2), pageable, 2);

        given(seatGradeRepository.findAllByPerformanceSessionId(1L, pageable))
                .willReturn(seatGradePage);

        Page<SeatGradeResponse> response = seatGradeService.findAll(1L, pageable);

        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent()).hasSize(2);

        SeatGradeResponse first = response.getContent().get(0);
        assertThat(first.getSeatGradeId()).isEqualTo(1L);
        assertThat(first.getSessionId()).isEqualTo(1L);
        assertThat(first.getGradeName()).isEqualTo(GradeName.VIP);
        assertThat(first.getPrice()).isEqualByComparingTo(new BigDecimal(10000));
        assertThat(first.getTotalSeats()).isEqualTo(100);
        assertThat(first.getRemainingSeats()).isEqualTo(50);

        SeatGradeResponse second = response.getContent().get(1);
        assertThat(second.getSeatGradeId()).isEqualTo(2L);
        assertThat(second.getGradeName()).isEqualTo(GradeName.R);
        assertThat(second.getPrice()).isEqualByComparingTo(new BigDecimal(8000));
        assertThat(second.getTotalSeats()).isEqualTo(200);
        assertThat(second.getRemainingSeats()).isEqualTo(150);
    }

    @Test
    @DisplayName("좌석 등급 전체 조회 성공 - 빈 목록")
    void findAll_success_emptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SeatGrade> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        given(seatGradeRepository.findAllByPerformanceSessionId(1L, pageable))
                .willReturn(emptyPage);

        Page<SeatGradeResponse> response = seatGradeService.findAll(1L, pageable);

        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getContent()).isEmpty();
    }

    @Test
    @DisplayName("좌석 등급 전체 조회 성공 - 페이지네이션")
    void findAll_success_pagination() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<SeatGrade> seatGradePage = new PageImpl<>(List.of(seatGrade), pageable, 2);

        given(seatGradeRepository.findAllByPerformanceSessionId(1L, pageable))
                .willReturn(seatGradePage);

        Page<SeatGradeResponse> response = seatGradeService.findAll(1L, pageable);

        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getSeatGradeId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("좌석 등급 단건 조회 성공")
    void findOne_success() {
        given(seatGradeRepository.findByIdAndPerformanceSessionId(1L, 1L))
                .willReturn(Optional.of(seatGrade));

        SeatGradeResponse response = seatGradeService.findOne(1L, 1L);

        assertThat(response.getSeatGradeId()).isEqualTo(1L);
        assertThat(response.getSessionId()).isEqualTo(1L);
        assertThat(response.getGradeName()).isEqualTo(GradeName.VIP);
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal(10000));
        assertThat(response.getTotalSeats()).isEqualTo(100);
        assertThat(response.getRemainingSeats()).isEqualTo(50);
    }

    @Test
    @DisplayName("좌석 등급 단건 조회 실패 - 존재하지 않는 좌석 등급")
    void findOne_fail_seatGradeNotFound() {
        given(seatGradeRepository.findByIdAndPerformanceSessionId(999L, 1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> seatGradeService.findOne(1L, 999L))
                .isInstanceOf(SeatGradeException.class);
    }

    @Test
    @DisplayName("좌석 등급 단건 조회 실패 - 세션 ID 불일치")
    void findOne_fail_sessionIdMismatch() {
        given(seatGradeRepository.findByIdAndPerformanceSessionId(1L, 999L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> seatGradeService.findOne(999L, 1L))
                .isInstanceOf(SeatGradeException.class);
    }
}
