package com.example.ticketingproject.domain.seatgrade.service;

import com.example.ticketingproject.common.enums.GradeName;

import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.seatgrade.dto.CreateSeatGradeRequest;
import com.example.ticketingproject.domain.seatgrade.dto.PutSeatGradeRequest;
import com.example.ticketingproject.domain.seatgrade.dto.SeatGradeResponse;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.exeption.SeatGradeException;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.seatgrade.service.AdminSeatGradeService;
import com.example.ticketingproject.domain.venue.entity.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminSeatGradeServiceTest {

    @Mock
    private SeatGradeRepository seatGradeRepository;

    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    @InjectMocks
    private AdminSeatGradeService adminSeatGradeService;

    private SeatGrade seatGrade;
    private PerformanceSession  performanceSession;
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
    }

    @Test
    @DisplayName("좌석 등급 생성 성공")
    void save_success() {
        CreateSeatGradeRequest request = CreateSeatGradeRequest.builder()
                .gradeName(GradeName.VIP)
                .price(new BigDecimal(10000))
                .totalSeats(100)
                .remainingSeats(50)
                .build();

        given(performanceSessionRepository.findById(1L)).willReturn(Optional.of(performanceSession));
        given(seatGradeRepository.save(any(SeatGrade.class))).willReturn(seatGrade);

        SeatGradeResponse response = adminSeatGradeService.save(1L, request);

        assertThat(response.getSeatGradeId()).isEqualTo(1L);
        assertThat(response.getSessionId()).isEqualTo(1L);
        assertThat(response.getGradeName()).isEqualTo(GradeName.VIP);
        assertThat(response.getTotalSeats()).isEqualTo(100);
        assertThat(response.getRemainingSeats()).isEqualTo(50);
    }

    @Test
    @DisplayName("좌석 등급 생성 실패")
    void save_fail() {
        CreateSeatGradeRequest request = CreateSeatGradeRequest.builder()
                .gradeName(GradeName.VIP)
                .price(new BigDecimal(10000))
                .totalSeats(100)
                .remainingSeats(50)
                .build();

        given(performanceSessionRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminSeatGradeService.save(999L, request))
                .isInstanceOf(PerformanceSessionException.class);
    }

    @Test
    @DisplayName("좌석 등급 수정 성공")
    void update_success() {
        PutSeatGradeRequest request = PutSeatGradeRequest.builder()
                .gradeName(GradeName.R)
                .build();

        given(seatGradeRepository.findByIdAndPerformanceSessionId(1L, 1L))
                .willReturn(Optional.of(seatGrade));

        SeatGradeResponse response = adminSeatGradeService.update(1L, 1L, request);

        assertThat(response.getSeatGradeId()).isEqualTo(1L);
        assertThat(response.getSessionId()).isEqualTo(1L);
        assertThat(response.getGradeName()).isEqualTo(GradeName.R);
    }

    @Test
    @DisplayName("좌석 등급 수정 실패 - 존재하지 않는 좌석 등급")
    void update_fail_seatGradeNotFound() {
        PutSeatGradeRequest request = PutSeatGradeRequest.builder()
                .gradeName(GradeName.R)
                .build();

        given(seatGradeRepository.findByIdAndPerformanceSessionId(999L, 1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> adminSeatGradeService.update(1L, 999L, request))
                .isInstanceOf(SeatGradeException.class);
    }

    @Test
    @DisplayName("좌석 등급 수정 실패 - 세션 ID 불일치")
    void update_fail_sessionIdMismatch() {
        PutSeatGradeRequest request = PutSeatGradeRequest.builder()
                .gradeName(GradeName.R)
                .build();

        given(seatGradeRepository.findByIdAndPerformanceSessionId(1L, 999L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> adminSeatGradeService.update(999L, 1L, request))
                .isInstanceOf(SeatGradeException.class);
    }

    @Test
    @DisplayName("좌석 등급 삭제 성공")
    void delete_success() {
        given(seatGradeRepository.findByIdAndPerformanceSessionId(1L, 1L))
                .willReturn(Optional.of(seatGrade));

        assertThatNoException()
                .isThrownBy(() -> adminSeatGradeService.delete(1L, 1L));

        assertThat(seatGrade.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("좌석 등급 삭제 실패 - 존재하지 않는 좌석 등급")
    void delete_fail_seatGradeNotFound() {
        given(seatGradeRepository.findByIdAndPerformanceSessionId(999L, 1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> adminSeatGradeService.delete(1L, 999L))
                .isInstanceOf(SeatGradeException.class);
    }

    @Test
    @DisplayName("좌석 등급 삭제 실패 - 세션 ID 불일치")
    void delete_fail_sessionIdMismatch() {
        given(seatGradeRepository.findByIdAndPerformanceSessionId(1L, 999L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> adminSeatGradeService.delete(999L, 1L))
                .isInstanceOf(SeatGradeException.class);
    }
}

