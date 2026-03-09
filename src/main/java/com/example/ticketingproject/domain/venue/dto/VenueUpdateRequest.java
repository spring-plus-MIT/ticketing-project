package com.example.ticketingproject.domain.venue.dto;

import lombok.Getter;

@Getter
public class VenueUpdateRequest {
    private String name;
    private String address;
    private int totalSeats;
}
