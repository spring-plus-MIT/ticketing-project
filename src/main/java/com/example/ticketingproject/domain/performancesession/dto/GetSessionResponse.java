package com.example.ticketingproject.domain.performancesession.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetSessionResponse {
    private Long id;
    private String title;
    private String venueName;
    private LocalDateTime sessionDateTime;
}