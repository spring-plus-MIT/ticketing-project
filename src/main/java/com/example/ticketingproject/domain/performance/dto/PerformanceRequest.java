package com.example.ticketingproject.domain.performance.dto;

import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 20, message = "시즌은 1자 이상 20자 이하로 입력해주세요")
    private String season;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private LocalDate startDate;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private LocalDate endDate;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private PerformanceStatus status;
}