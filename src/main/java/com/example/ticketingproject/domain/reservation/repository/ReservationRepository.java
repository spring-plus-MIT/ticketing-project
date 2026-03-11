package com.example.ticketingproject.domain.reservation.repository;

import com.example.ticketingproject.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAllByUserId(Long userId, Pageable pageable);

    Optional<Reservation> findByIdAndUserId(Long reservationId, Long userId);

    // 예약 대기 30분, 스케줄러에 적용 해야 하는데 내일 할 예정
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.expiresAt < :now ")
    List<Reservation> findExpiredReservations(LocalDateTime now);

}