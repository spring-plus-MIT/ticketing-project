package com.example.ticketingproject.domain.work.repository;

import com.example.ticketingproject.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, Long> {
}
