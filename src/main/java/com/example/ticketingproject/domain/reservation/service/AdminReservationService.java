package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationService {

    private final UserRepository userRepository;

    private final ReservationRepository reservationRepository;

    public Page<ReservationResponse> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(ReservationResponse::from);
    }

    public Page<ReservationResponse> getReservationsByUser(Long userId, Pageable pageable) {
        if(!userRepository.existsById(userId)) throw new UserException(
                USER_NOT_FOUND.getHttpStatus(),
                USER_NOT_FOUND
        );
        return reservationRepository.findAllByUserId(userId, pageable)
                .map(ReservationResponse::from);
    }
}
