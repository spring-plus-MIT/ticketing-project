package com.example.ticketingproject.domain.seatgrade.repository;

import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Long> {

    // 특정 회차의 등급 중 삭제되지 않은 것만 조회
    List<SeatGrade> findAllByPerformanceSessionAndDeletedAtIsNull(PerformanceSession session);
}