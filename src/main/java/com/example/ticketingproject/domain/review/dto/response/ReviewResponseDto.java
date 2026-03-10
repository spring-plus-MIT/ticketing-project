package com.example.ticketingproject.domain.review.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponseDto {
    private Long id;
    private String content;
    private int rating;
    private String nickname;
    private LocalDateTime createdAt; // 목록 조회 시 시점 확인용
}