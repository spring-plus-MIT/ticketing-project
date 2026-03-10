package com.example.ticketingproject.domain.venue.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateVenueResponse {
    private final Long id;
    private final String name;
    private final String address;
    private final int totalSeats;
}
