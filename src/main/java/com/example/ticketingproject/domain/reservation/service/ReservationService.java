package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationRequestDto;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponseDto;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
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
    public ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException(ErrorStatus.USER_NOT_FOUND));

        // 2. 공연 회차 조회
        PerformanceSession session = performanceSessionRepository.findById(requestDto.getPerformanceSessionId())
                .orElseThrow(() -> new ReservationException(ErrorStatus.SESSION_NOT_FOUND));

        // 3. 좌석 조회
        Seat seat = seatRepository.findById(requestDto.getSeatId())
                .orElseThrow(() -> new ReservationException(ErrorStatus.SEAT_NOT_FOUND));

        // 4. 좌석 등급 가격 정보 조회
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
                // ⭐ 60번째 줄: getGradePrice() 대신 getPrice()를 사용합니다.
                .totalPrice(seatGrade.getPrice())
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        // 6. DB 저장 및 결과 반환
        return ReservationResponseDto.from(reservationRepository.save(reservation));
    }
}