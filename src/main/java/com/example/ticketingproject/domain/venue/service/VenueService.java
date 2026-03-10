package com.example.ticketingproject.domain.venue.service;

import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {
    private final VenueRepository venueRepository;

    public Page<VenueResponse> getVenues(Pageable pageable) {
        Page<Venue> venues = venueRepository.findAll(pageable);
        return venues.map(VenueResponse::from);
    }
}
