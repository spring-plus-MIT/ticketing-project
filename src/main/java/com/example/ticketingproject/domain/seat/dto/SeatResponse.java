package com.example.ticketingproject.domain.seat.dto;

import com.example.ticketingproject.common.enums.GradeName;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SeatResponse {

    private final Long seatId;
    private final Long venueId;
    private final GradeName gradeName;
    private final String rowName;
    private final int seatNumber;
    private final LocalDateTime createdAt;
}
