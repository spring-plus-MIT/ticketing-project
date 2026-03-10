package com.example.ticketingproject.domain.seatgrade.repository;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Long> {

    Optional<SeatGrade> findByPerformanceSessionIdAndGradeNameAndDeletedAtIsNull(Long sessionId, GradeName gradeName);
    Page<SeatGrade> findAllByPerformanceSessionIdAndDeletedAtIsNull(Long SessionId, Pageable pageable);
    Optional<SeatGrade> findByIdAndPerformanceSessionIdAndDeletedAtIsNull(Long seatGradeId, Long sessionId);
}