package com.example.ticketingproject.domain.reservation.repository;

import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.seat.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 서비스 49번 라인 에러 해결을 위한 메서드
    boolean existsByPerformanceSessionAndSeatAndStatusNot(
            PerformanceSession session,
            Seat seat,
            ReservationStatus status
    );

    Page<Reservation> findAllByUserId(Long userId, Pageable pageable);

    Optional<Reservation> findByIdAndUserId(Long id, Long userId);
}