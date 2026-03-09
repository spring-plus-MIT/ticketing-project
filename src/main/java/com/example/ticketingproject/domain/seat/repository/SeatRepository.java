package com.example.ticketingproject.domain.seat.repository;

import com.example.ticketingproject.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
