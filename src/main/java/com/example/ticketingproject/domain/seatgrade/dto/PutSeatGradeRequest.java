package com.example.ticketingproject.domain.seatgrade.dto;

import com.example.ticketingproject.common.enums.GradeName;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PutSeatGradeRequest {

    private GradeName gradeName;
}
