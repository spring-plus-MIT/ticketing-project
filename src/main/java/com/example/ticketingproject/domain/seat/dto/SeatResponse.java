package com.example.ticketingproject.domain.seat.dto;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.seat.entity.Seat;
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

    public static SeatResponse from(Seat seat) {
        return SeatResponse.builder()
                .seatId(seat.getId())
                .venueId(seat.getVenue().getId())
                .gradeName(seat.getSeatGrade().getGradeName())
                .rowName(seat.getRowName())
                .seatNumber(seat.getSeatNumber())
                .createdAt(seat.getCreatedAt())
                .build();
    }
}
