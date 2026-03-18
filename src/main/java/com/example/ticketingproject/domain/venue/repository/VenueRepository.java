package com.example.ticketingproject.domain.venue.repository;

import com.example.ticketingproject.domain.venue.entity.Venue;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Venue  v WHERE  v.id = :venueId")
    Optional<Venue> findByIdWithLock(Long venueId);
}
