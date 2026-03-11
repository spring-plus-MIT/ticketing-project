package com.example.ticketingproject.domain.charge.repository;

import com.example.ticketingproject.domain.charge.entity.Charge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    Page<Charge> findAllByUserId(Long userId, Pageable pageable);
}
