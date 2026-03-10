package com.example.ticketingproject.domain.review.dto.RequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private String content;
    private Integer rating;
}