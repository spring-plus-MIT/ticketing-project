package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.exception.SeatException;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.redis.lock.annotation.RedisLock;
import com.example.ticketingproject.redis.lock.enums.LockStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final PerformanceSessionRepository performanceSessionRepository;

    @Transactional
    @RedisLock(
            key = "'lock:session:' + #requestDto.performanceSessionId + ':seat:' + #requestDto.seatId",
            strategy = LockStrategy.FAIL_FAST
    )
    public ReservationResponse createReservation(ReservationCreateRequest requestDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                        )
                );

        PerformanceSession performanceSession = performanceSessionRepository.findById(requestDto.getPerformanceSessionId())
                .orElseThrow(()->new PerformanceSessionException(
                        ErrorStatus.SESSION_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.SESSION_NOT_FOUND
                        )
                );

        Seat seat = seatRepository.findById(requestDto.getSeatId())
                .orElseThrow(() -> new SeatException(
                                ErrorStatus.SEAT_NOT_FOUND.getHttpStatus(),
                                ErrorStatus.SEAT_NOT_FOUND
                        )
                );

        // 좌석 점유를 위한 seat 메서드 추가 (SeatStatus = RESERVED)
        seat.reserve();

        Reservation reservation = Reservation.builder()
                .user(user)
                .performanceSession(performanceSession)
                .seat(seat)
                .status(ReservationStatus.PENDING)
                .totalPrice(seat.getSeatGrade().getPrice())
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.from(savedReservation);
    }

    public ReservationResponse findOneReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new ReservationException(ErrorStatus.RESERVATION_NOT_FOUND));

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new ReservationException(ErrorStatus.RESERVATION_NOT_FOUND));

        reservation.cancel();

        reservation.getSeat().release();
    }

    @Transactional
    public void cancelExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, now);

        for (Reservation reservation : expiredReservations) {
            reservation.cancel();
            reservation.getSeat().release();
        }
    }
}
