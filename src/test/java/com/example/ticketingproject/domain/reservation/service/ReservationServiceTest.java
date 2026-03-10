package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationRequestDto;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponseDto;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private PerformanceSessionRepository performanceSessionRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private SeatGradeRepository seatGradeRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        // ⭐ InjectMocks 대신 수동으로 생성자에 주입해서 null 방지
        reservationService = new ReservationService(
                reservationRepository,
                userRepository,
                performanceSessionRepository,
                seatRepository,
                seatGradeRepository
        );
    }

    @Test
    @DisplayName("예약 생성 성공 테스트")
    void createReservation_Success() {
        // given
        Long userId = 1L;
        ReservationRequestDto requestDto = new ReservationRequestDto(1L, 1L);

        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);

        Work work = Work.builder()
                .title("레미제라블")
                .build();

        Performance performance = Performance.builder()
                .work(work)
                .build();

        PerformanceSession session = PerformanceSession.builder()
                .performance(performance)
                .build();
        ReflectionTestUtils.setField(session, "id", 1L);

        Seat seat = Seat.builder()
                .gradeName(GradeName.VIP)
                .build();

        SeatGrade seatGrade = SeatGrade.builder()
                .price(BigDecimal.valueOf(100000))
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(performanceSessionRepository.findById(any())).willReturn(Optional.of(session));
        given(seatRepository.findById(any())).willReturn(Optional.of(seat));
        given(seatGradeRepository.findByPerformanceSessionIdAndGradeNameAndDeletedAtIsNull(any(), any()))
                .willReturn(Optional.of(seatGrade));

        given(reservationRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ReservationResponseDto response = reservationService.createReservation(requestDto, userId);

        // then
        assertNotNull(response);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("실패 테스트 - 유저 없음")
    void createReservation_UserNotFound() {
        given(userRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(ReservationException.class, () ->
                reservationService.createReservation(new ReservationRequestDto(1L, 1L), 1L)
        );
    }
}