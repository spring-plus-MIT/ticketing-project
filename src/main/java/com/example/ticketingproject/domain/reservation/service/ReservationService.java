package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.exception.SeatException;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SeatGradeRepository seatGradeRepository;

    @Transactional
    public ReservationResponse createReservation(ReservationCreateRequest requestDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                ));

        SeatGrade seatGrade = seatGradeRepository.findById(requestDto.getSeatId())
                .orElseThrow(() -> new SeatException(
                        ErrorStatus.SEAT_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.SEAT_NOT_FOUND
                ));


        Seat seat = seatGrade.getSeat();

        Reservation reservation = Reservation.builder()
                .user(user)
                .performance(seatGrade.getPerformanceSession().getPerformance())
                .seat(seat)
                .status(ReservationStatus.RESERVED)
                .totalPrice(seatGrade.getPrice())
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponse.from(savedReservation);
    }
}
