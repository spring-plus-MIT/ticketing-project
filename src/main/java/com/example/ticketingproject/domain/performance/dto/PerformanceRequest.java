package com.example.ticketingproject.domain.performance.dto;

import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
public class PerformanceRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private Long workId;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private Long venueId;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Length(min = 1, max = 20, message = MSG_VALIDATION_LENGTH_ERROR)
    private String season;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private LocalDate startDate;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private LocalDate endDate;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private PerformanceStatus status;
}