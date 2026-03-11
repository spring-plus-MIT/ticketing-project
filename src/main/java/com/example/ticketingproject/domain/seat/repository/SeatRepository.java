package com.example.ticketingproject.domain.seat.repository;

import com.example.ticketingproject.domain.seat.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SeatRepository extends JpaRepository<Seat, Long> {
    Page<Seat> findAllByVenueId(Long venueId, Pageable pageable);
    Optional<Seat> findByIdAndVenueId(Long seatId, Long venueId);
    int countByVenueId(Long venueId);
}
