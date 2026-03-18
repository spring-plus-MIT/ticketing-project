package com.example.ticketingproject.domain.venue.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;

@Getter
public class UpdateVenueRequest {

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    private String name;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    private String address;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    private int totalSeats;
}
