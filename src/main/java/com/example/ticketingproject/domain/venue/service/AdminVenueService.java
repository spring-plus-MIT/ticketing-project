package com.example.ticketingproject.domain.venue.service;

import com.example.ticketingproject.domain.venue.dto.CreateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminVenueService {
    private final VenueRepository venueRepository;

    @Transactional
    public VenueResponse create(CreateVenueRequest request) {
        Venue venue = Venue.builder()
                .name(request.getName())
                .address(request.getAddress())
                .totalSeats(request.getTotalSeats())
                .build();

        Venue savedVenue = venueRepository.save(venue);

        return VenueResponse.builder()
                        .id(savedVenue.getId())
                        .name(savedVenue.getName())
                        .address(savedVenue.getAddress())
                        .totalSeats(savedVenue.getTotalSeats())
                        .build();
    }
}
