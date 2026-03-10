package com.example.ticketingproject.domain.venue.dto;

import lombok.Getter;

@Getter
public class UpdateVenueRequest {
    private String name;
    private String address;
    private int totalSeats;
}
