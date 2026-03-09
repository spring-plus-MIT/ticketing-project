package com.example.ticketingproject.domain.performance.repository;

import com.example.ticketingproject.domain.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
