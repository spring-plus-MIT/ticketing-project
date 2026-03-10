package com.example.ticketingproject.domain.venue.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateVenueRequest {

    @Size(min = 1, max = 100)
    private String name;

    @Max(255)
    private String address;

    @Min(1)
    private int totalSeats;
}
