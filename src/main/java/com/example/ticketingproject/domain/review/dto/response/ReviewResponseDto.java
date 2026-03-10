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
    private String userName; // nickname 대신 userName으로 변경
    private LocalDateTime createdAt;

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .userName(review.getUser().getName()) // Review 엔티티에는 nickname 없음, User 이름으로 대체
                .createdAt(review.getCreatedAt())
                .build();
    }
}
