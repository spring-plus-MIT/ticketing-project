package com.example.ticketingproject.domain.venue.dto;

import com.example.ticketingproject.domain.venue.entity.Venue;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VenueResponse {
    private final Long id;
    private final String name;
    private final String address;
    private final int totalSeats;

    public static VenueResponse from(Venue venue) {
        return VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .totalSeats(venue.getTotalSeats())
                .build();
    }
}
