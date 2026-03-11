package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
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
    private AdminReservationService adminReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 관리자_예약상태_변경() {

        Long reservationId = 1L;

        Reservation reservation = mock(Reservation.class);

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        adminReservationService.updateReservationStatusByAdmin(
                reservationId,
                ReservationStatus.CONFIRMED
        );

        verify(reservationRepository).findById(reservationId);
        verify(reservation).updateStatus(ReservationStatus.CONFIRMED);
    }
}
