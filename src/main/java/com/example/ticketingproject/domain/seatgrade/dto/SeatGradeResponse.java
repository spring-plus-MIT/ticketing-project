package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class SeatGradeResponse {

    private final Long seatGradeId;
    private final Long sessionId;
    private final GradeName gradeName;
    private final BigDecimal price;
    private final int totalSeats;
    private final int remainingSeats;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final LocalDateTime deletedAt;
}
