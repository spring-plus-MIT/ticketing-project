package com.example.ticketingproject.domain.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(min = 10, max = 500)
    private String content;

    @Min(1) @Max(5)
    private int rating;

    public ReviewRequestDto(String 내용, int i) {
    }
}