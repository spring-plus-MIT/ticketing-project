package com.example.ticketingproject.domain.seatgrade.repository;

import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Long> {

    Page<SeatGrade> findAllByPerformanceSessionIdAndDeletedAtIsNull(Long SessionId, Pageable pageable);
    Optional<SeatGrade> findByIdAndSessionIdAndDeletedAtIsNull(Long seatGradeId, Long sessionId);
}