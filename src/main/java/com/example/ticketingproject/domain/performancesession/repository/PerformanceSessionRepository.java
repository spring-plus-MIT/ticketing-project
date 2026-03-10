package com.example.ticketingproject.domain.performancesession.repository;

import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceSessionRepository extends JpaRepository<PerformanceSession, Long> {
}
