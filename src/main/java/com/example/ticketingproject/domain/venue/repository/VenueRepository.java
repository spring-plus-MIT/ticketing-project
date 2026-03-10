package com.example.ticketingproject.domain.venue.repository;

import com.example.ticketingproject.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
