package com.example.ticketingproject.domain.seatgrade.repository;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Long> {

    Page<SeatGrade> findAllByPerformanceSessionId(Long sessionId, Pageable pageable);
    Optional<SeatGrade> findByIdAndPerformanceSessionId(Long seatGradeId, Long sessionId);
    Optional<SeatGrade> findByGradeName(GradeName gradeName);
}