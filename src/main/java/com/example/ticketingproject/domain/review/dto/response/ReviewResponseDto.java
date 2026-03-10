package com.example.ticketingproject.domain.review.dto.response;

import com.example.ticketingproject.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponseDto {

    private Long id;
    private String content;
    private int rating;
    private String userName;
    private LocalDateTime createdAt;

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .userName(review.getUser().getName())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
