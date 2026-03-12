package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class AdminReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

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
}
