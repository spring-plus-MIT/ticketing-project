package com.example.ticketingproject.domain.performance.repository;

import com.example.ticketingproject.domain.performance.entity.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Override
    @EntityGraph(attributePaths = {"work", "venue"})
    Page<Performance> findAll(Pageable pageable);

}
