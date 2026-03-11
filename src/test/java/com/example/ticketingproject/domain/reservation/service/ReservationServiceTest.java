package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 예약_생성_성공() {

        Long userId = 1L;
        Long seatId = 1L;
        Long performnaceSessionId = 1L;

        ReservationCreateRequest request = new ReservationCreateRequest();
        ReflectionTestUtils.setField(request, "performanceSessionId", performnaceSessionId);
        ReflectionTestUtils.setField(request, "seatId", seatId);

        User user = mock(User.class);

        SeatGrade seatGrade = mock(SeatGrade.class);
        when(seatGrade.getPrice()).thenReturn(BigDecimal.valueOf(10000));
        when(seatGrade.getGradeName()).thenReturn(GradeName.VIP); // 추가된 부분

        Seat seat = mock(Seat.class);
        when(seat.getSeatGrade()).thenReturn(seatGrade);

        PerformanceSession performanceSession = mock(PerformanceSession.class);
        Performance performance = mock(Performance.class);
        Work work = mock(Work.class);

        when(performance.getWork()).thenReturn(work);
        when(work.getTitle()).thenReturn("테스트 공연");

        Reservation reservation = mock(Reservation.class);

        // NPE 방지용 mock 세팅
        when(reservation.getUser()).thenReturn(user);
        when(reservation.getSeat()).thenReturn(seat);
        when(reservation.getPerformanceSession()).thenReturn(performanceSession);
        when(performanceSession.getPerformance()).thenReturn(performance);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(performanceSessionRepository.findById(performnaceSessionId)).thenReturn(Optional.of(performanceSession));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationResponse response = reservationService.createReservation(request, userId);

        verify(userRepository).findById(userId);
        verify(seatRepository).findById(seatId);
        verify(reservationRepository).save(any());

        assertThat(response).isNotNull();
    }
}
