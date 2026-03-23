package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AdminReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @InjectMocks
    private AdminReservationService adminReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 관리자_예약_취소_성공_테스트() {

        Long reservationId = 1L;
        Long userId = 1L;

        Reservation reservation = mock(Reservation.class);
        Seat seat = mock(Seat.class);

        when(reservationRepository.findByIdAndUserId(reservationId, userId))
                .thenReturn(Optional.of(reservation));
        when(reservation.getSeat()).thenReturn(seat);

        reservationService.cancelReservation(
                reservationId, userId
        );

        verify(reservationRepository).findByIdAndUserId(reservationId, userId);
        verify(reservation).cancel();
        verify(seat).release();
    }
    @Test
    @DisplayName("관리자 - 전체 예약 목록 조회 테스트")
    void getAllReservationsTest() {
        Work mockWork = Work.builder().build();
        Performance mockPerformance = Performance.builder().work(mockWork).build();
        PerformanceSession mockSession = PerformanceSession.builder().performance(mockPerformance).build();

        SeatGrade mockGrade = SeatGrade.builder().build();
        ReflectionTestUtils.setField(mockGrade, "gradeName", GradeName.values()[0]);
        ReflectionTestUtils.setField(mockGrade, "id", 1L);

        Seat mockSeat = Seat.builder().seatNumber(1).build();
        ReflectionTestUtils.setField(mockSeat, "seatGrade", mockGrade);
        ReflectionTestUtils.setField(mockSeat, "id", 1L);

        User mockUser = User.builder().build();
        ReflectionTestUtils.setField(mockUser, "id", 1L);

        Reservation reservation = Reservation.builder()
                .user(mockUser)
                .performanceSession(mockSession)
                .seat(mockSeat)
                .build();
        ReflectionTestUtils.setField(reservation, "id", 1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation));
        when(reservationRepository.findAll(pageable)).thenReturn(reservationPage);

        Page<ReservationResponse> result = adminReservationService.getAllReservations(pageable);

        assertThat(result).isNotNull();
        verify(reservationRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("관리자 - 특정 유저의 예약 목록 조회 테스트")
    void getReservationsByUserTest() {
        Work mockWork = Work.builder().build();
        Performance mockPerformance = Performance.builder().work(mockWork).build();
        PerformanceSession mockSession = PerformanceSession.builder().performance(mockPerformance).build();

        SeatGrade mockGrade = SeatGrade.builder().build();
        ReflectionTestUtils.setField(mockGrade, "gradeName", GradeName.values()[0]);
        ReflectionTestUtils.setField(mockGrade, "id", 1L);

        Seat mockSeat = Seat.builder().seatNumber(1).build();
        ReflectionTestUtils.setField(mockSeat, "seatGrade", mockGrade);
        ReflectionTestUtils.setField(mockSeat, "id", 1L);

        Long userId = 1L;
        User mockUser = User.builder().build();
        ReflectionTestUtils.setField(mockUser, "id", userId);

        Reservation reservation = Reservation.builder()
                .user(mockUser)
                .performanceSession(mockSession)
                .seat(mockSeat)
                .build();
        ReflectionTestUtils.setField(reservation, "id", 1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(reservationRepository.findAllByUserId(userId, pageable)).thenReturn(reservationPage);

        Page<ReservationResponse> result = adminReservationService.getReservationsByUser(userId, pageable);

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).existsById(userId);
        verify(reservationRepository, times(1)).findAllByUserId(userId, pageable);
    }

    @Test
    @DisplayName("관리자 - 존재하지 않는 유저의 예약 목록 조회 시 UserException 발생")
    void getReservationsByUser_존재하지_않는_유저_실패() {
        Long userId = 99L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> adminReservationService.getReservationsByUser(userId, pageable))
                .isInstanceOf(UserException.class);

        verify(userRepository, times(1)).existsById(userId);
        verify(reservationRepository, never()).findAllByUserId(any(), any());
    }

    @Test
    void 예약_단건_조회_성공_테스트() {
        // given
        Work mockWork = Work.builder().title("테스트 공연").build();
        Performance mockPerformance = Performance.builder().work(mockWork).build();
        PerformanceSession mockSession = PerformanceSession.builder().performance(mockPerformance).build();

        SeatGrade mockGrade = SeatGrade.builder().build();
        ReflectionTestUtils.setField(mockGrade, "gradeName", GradeName.values()[0]);
        ReflectionTestUtils.setField(mockGrade, "id", 1L);

        Seat mockSeat = Seat.builder().seatNumber(1).build();
        ReflectionTestUtils.setField(mockSeat, "seatGrade", mockGrade);
        ReflectionTestUtils.setField(mockSeat, "id", 1L);

        User mockUser = User.builder().build();
        ReflectionTestUtils.setField(mockUser, "id", 1L);

        Reservation reservation = Reservation.builder()
                .user(mockUser)
                .performanceSession(mockSession)
                .seat(mockSeat)
                .status(ReservationStatus.PENDING)
                .build();

        ReflectionTestUtils.setField(reservation, "id", 1L);

        when(reservationRepository.findByIdAndUserId(reservation.getId(), mockUser.getId())).thenReturn(Optional.of(reservation));

        // when
        ReservationResponse response = reservationService.findOneReservation(mockUser.getId(), reservation.getId());

        // then
        verify(reservationRepository).findByIdAndUserId(reservation.getId(), mockUser.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(reservation.getId());
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(response.getPerformanceTitle()).isEqualTo("테스트 공연");
    }
}
