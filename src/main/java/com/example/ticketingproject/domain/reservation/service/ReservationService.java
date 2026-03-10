package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PerformanceSessionRepository performanceSessionRepository;
    private final SeatRepository seatRepository;
    private final SeatGradeRepository seatGradeRepository;

    @Transactional
    public ReservationResponse createReservation(ReservationCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException(ErrorStatus.USER_NOT_FOUND));

        PerformanceSession session = performanceSessionRepository.findById(request.getPerformanceSessionId())
                .orElseThrow(() -> new ReservationException(ErrorStatus.SESSION_NOT_FOUND));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ReservationException(ErrorStatus.SEAT_NOT_FOUND));

        SeatGrade seatGrade = seatGradeRepository.findByPerformanceSessionIdAndGradeNameAndDeletedAtIsNull(
                session.getId(),
                seat.getGradeName()
        ).orElseThrow(() -> new ReservationException(ErrorStatus.SEAT_GRADE_NOT_FOUND));

        // 5. 예약 객체 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .performanceSession(session)
                .seat(seat)
                .status(ReservationStatus.PENDING)
                .totalPrice(seatGrade.getPrice())
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }
}