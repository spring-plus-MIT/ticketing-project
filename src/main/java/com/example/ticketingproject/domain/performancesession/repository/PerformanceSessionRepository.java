package com.example.ticketingproject.domain.performancesession.repository;

import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.venue.entity.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface PerformanceSessionRepository extends JpaRepository<PerformanceSession, Long> {

    Page<PerformanceSession> findByPerformanceId(Long performanceId, Pageable pageable);

    @Query("SELECT COUNT(ps) > 0 FROM PerformanceSession ps " +
            "WHERE ps.venue = :venue " +
            "AND ps.sessionDateTime = :time " +
            "AND ps.deletedAt IS NULL") // deleted_at -> deletedAt 으로 수정
    boolean existsByVenueAndSessionDateAndSessionTime(
            @Param("venue") Venue venue,
            @Param("datetime") LocalDateTime datetime
    );

}
