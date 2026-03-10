package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponseDto;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.work.entity.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    private AdminReservationService adminReservationService;

    @BeforeEach
    void setUp() {
        adminReservationService = new AdminReservationService(reservationRepository);
    }

    private Reservation createReservation() {

        Work work = Work.builder()
                .title("레미제라블")
                .build();

        Performance performance = Performance.builder()
                .work(work)
                .build();

        PerformanceSession session = PerformanceSession.builder()
                .performance(performance)
                .startTime(LocalDateTime.now())
                .build();

        Seat seat = Seat.builder()
                .seatNumber(1)
                .build();

        User user = User.builder().build();

        Reservation reservation = Reservation.builder()
                .user(user)
                .performanceSession(session)
                .seat(seat)
                .status(ReservationStatus.PENDING)
                .totalPrice(BigDecimal.valueOf(100000))
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        ReflectionTestUtils.setField(reservation, "id", 1L);

        return reservation;
    }

    @Test
    @DisplayName("관리자 전체 예약 조회 성공")
    void getAllReservations_Success() {

        Reservation reservation = createReservation();

        Page<Reservation> page =
                new PageImpl<>(List.of(reservation), PageRequest.of(0, 10), 1);

        given(reservationRepository.findAll(PageRequest.of(0,10))).willReturn(page);

        Page<ReservationResponseDto> result =
                adminReservationService.getAllReservations(PageRequest.of(0,10));

        assertEquals(1, result.getTotalElements());
        verify(reservationRepository).findAll(PageRequest.of(0,10));
    }

    @Test
    @DisplayName("특정 유저 예약 조회 성공")
    void getReservationsByUser_Success() {

        Long userId = 1L;

        Reservation reservation = createReservation();

        Page<Reservation> page =
                new PageImpl<>(List.of(reservation), PageRequest.of(0, 10), 1);

        given(reservationRepository.findAllByUserId(userId, PageRequest.of(0,10)))
                .willReturn(page);

        Page<ReservationResponseDto> result =
                adminReservationService.getReservationsByUser(userId, PageRequest.of(0,10));

        assertEquals(1, result.getTotalElements());
        verify(reservationRepository).findAllByUserId(userId, PageRequest.of(0,10));
    }

    @Test
    @DisplayName("관리자 예약 상태 수정 - 예약 없음")
    void updateReservationStatusByAdmin_NotFound() {

        given(reservationRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThrows(ReservationException.class, () ->
                adminReservationService.updateReservationStatusByAdmin(1L, ReservationStatus.CONFIRMED)
        );
    }

    @Test
    @DisplayName("관리자 예약 상태 수정 성공")
    void updateReservationStatusByAdmin_Success() {

        Reservation reservation = createReservation();

        given(reservationRepository.findById(1L))
                .willReturn(Optional.of(reservation));

        adminReservationService.updateReservationStatusByAdmin(1L, ReservationStatus.CONFIRMED);

        verify(reservationRepository).findById(1L);
    }
}
