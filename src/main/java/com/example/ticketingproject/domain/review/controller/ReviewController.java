package com.example.ticketingproject.domain.review.controller;

import com.example.ticketingproject.domain.review.dto.RequestDto.ReviewRequestDto;
import com.example.ticketingproject.domain.review.service.ReviewService;
import com.example.ticketingproject.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/works/{workId}/reviews") // 명세서 기반 경로
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 1. 리뷰 생성
    @PostMapping
    public ResponseEntity<Long> createReview(
            @PathVariable Long workId,
            @RequestBody ReviewRequestDto requestDto // DTO는 아래에 따로 만들어둘게요
    ) {
        // TODO: 실제 프로젝트라면 시큐리티에서 인증된 유저 ID를 가져와야 합니다.
        // 일단은 테스트를 위해 임시로 1L을 넣었습니다.
        Long userId = 1L;
        Long reviewId = reviewService.createReview(userId, workId, requestDto.getContent(), requestDto.getRating());
        return ResponseEntity.ok(reviewId);
    }

    // 2. 특정 작품의 리뷰 목록 조회
    @GetMapping
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long workId) {
        List<Review> reviews = reviewService.getReviewsByWork(workId);
        return ResponseEntity.ok(reviews);
    }

    // 3. 리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto requestDto
    ) {
        Long userId = 1L; // 임시 유저 ID
        reviewService.updateReview(userId, reviewId, requestDto.getContent(), requestDto.getRating());
        return ResponseEntity.noContent().build();
    }

    // 4. 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        Long userId = 1L; // 임시 유저 ID
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }
}