package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.redis.lock.service.LockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private LockService lockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ReservationResponse.from()이 접근하는 모든 메서드를 stubbing한 mock Reservation 반환
    private Reservation createMockReservation(User user, Seat seat, PerformanceSession performanceSession) {
        Reservation reservation = mock(Reservation.class);
        when(reservation.getId()).thenReturn(1L);
        when(reservation.getUser()).thenReturn(user);
        when(reservation.getSeat()).thenReturn(seat);
        when(reservation.getPerformanceSession()).thenReturn(performanceSession);
        when(reservation.getStatus()).thenReturn(ReservationStatus.PENDING);
        when(reservation.getTotalPrice()).thenReturn(BigDecimal.valueOf(10000));
        return reservation;
    }

    private User createMockUser(Long userId) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        return user;
    }

    private Seat createMockSeat() {
        SeatGrade seatGrade = mock(SeatGrade.class);
        when(seatGrade.getGradeName()).thenReturn(GradeName.VIP);
        when(seatGrade.getPrice()).thenReturn(BigDecimal.valueOf(10000));

        Seat seat = mock(Seat.class);
        when(seat.getSeatGrade()).thenReturn(seatGrade);
        when(seat.getRowName()).thenReturn("A");
        when(seat.getSeatNumber()).thenReturn(1);
        return seat;
    }

    private PerformanceSession createMockPerformanceSession() {
        Work work = mock(Work.class);
        when(work.getTitle()).thenReturn("테스트 공연");

        Performance performance = mock(Performance.class);
        when(performance.getWork()).thenReturn(work);
        when(performance.getStartDate()).thenReturn(LocalDate.of(2026, 5, 1));

        PerformanceSession performanceSession = mock(PerformanceSession.class);
        when(performanceSession.getPerformance()).thenReturn(performance);
        when(performanceSession.getStartTime()).thenReturn(LocalDateTime.of(2026, 5, 1, 19, 0));
        return performanceSession;
    }

    @Test
    void 예약_생성_성공() {
        // given
        Long userId = 1L;
        Long seatId = 1L;
        Long performanceSessionId = 1L;

        ReservationCreateRequest request = new ReservationCreateRequest();
        ReflectionTestUtils.setField(request, "performanceSessionId", performanceSessionId);
        ReflectionTestUtils.setField(request, "seatId", seatId);

        User user = createMockUser(userId);
        Seat seat = createMockSeat();
        PerformanceSession performanceSession = createMockPerformanceSession();
        Reservation savedReservation = createMockReservation(user, seat, performanceSession);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(performanceSessionRepository.findById(performanceSessionId)).thenReturn(Optional.of(performanceSession));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(reservationRepository.save(any())).thenReturn(savedReservation);

        // when
        ReservationResponse response = reservationService.createReservation(request, userId);

        // then
        verify(userRepository).findById(userId);
        verify(performanceSessionRepository).findById(performanceSessionId);
        verify(seatRepository).findById(seatId);
        verify(seat).reserve();
        verify(reservationRepository).save(any());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getPerformanceTitle()).isEqualTo("테스트 공연");
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void 예약_단건_조회_성공() {
        // given
        Long userId = 1L;
        Long reservationId = 1L;

        User user = createMockUser(userId);
        Seat seat = createMockSeat();
        PerformanceSession performanceSession = createMockPerformanceSession();
        Reservation reservation = createMockReservation(user, seat, performanceSession);

        when(reservationRepository.findByIdAndUserId(reservationId, userId)).thenReturn(Optional.of(reservation));

        // when
        ReservationResponse response = reservationService.findOneReservation(userId, reservationId);

        // then
        verify(reservationRepository).findByIdAndUserId(reservationId, userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(reservationId);
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(response.getPerformanceTitle()).isEqualTo("테스트 공연");
    }

    @Test
    void 예약_취소_성공() {
        // given
        Long userId = 1L;
        Long reservationId = 1L;

        Seat seat = mock(Seat.class);
        Reservation reservation = mock(Reservation.class);
        when(reservation.getSeat()).thenReturn(seat);
        when(reservationRepository.findByIdAndUserId(reservationId, userId)).thenReturn(Optional.of(reservation));

        // when
        reservationService.cancelReservation(reservationId, userId);

        // then
        verify(reservationRepository).findByIdAndUserId(reservationId, userId);
        verify(reservation).cancel();
        verify(seat).release();
    }


}
