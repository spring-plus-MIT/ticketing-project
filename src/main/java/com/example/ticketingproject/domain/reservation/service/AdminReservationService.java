package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponseDto;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * [관리자] 전체 예약 목록 조회
     */
    public Page<ReservationResponseDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(ReservationResponseDto::from);
    }

    /**
     * [관리자] 특정 유저의 예약 목록 조회
     */
    public Page<ReservationResponseDto> getReservationsByUser(Long userId, Pageable pageable) {
        return reservationRepository.findAllByUserId(userId, pageable)
                .map(ReservationResponseDto::from);
    }

    /**
     * [관리자] 예약 상태 강제 수정
     */
    @Transactional
    public void updateReservationStatusByAdmin(Long reservationId, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ErrorStatus.RESERVATION_NOT_FOUND));

        // 예: 이미 취소된 건 다시 확정할 수 없게 막는 등 로직 추가 가능
        // reservation.updateStatus(newStatus);
    }
}