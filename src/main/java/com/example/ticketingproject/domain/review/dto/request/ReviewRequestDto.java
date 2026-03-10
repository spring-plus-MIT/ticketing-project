package com.example.ticketingproject.domain.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_ERROR;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {

    @Size(min = 1, max = 200, message = MSG_VALIDATION_ERROR)
    private String content;

    @Min(value = 1, message = MSG_VALIDATION_ERROR)
    @Max(value = 5, message = MSG_VALIDATION_ERROR)
    private int rating;

}
