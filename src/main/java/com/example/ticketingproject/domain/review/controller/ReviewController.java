package com.example.ticketingproject.domain.review.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.review.dto.request.ReviewRequestDto;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.service.ReviewService;
import com.example.ticketingproject.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/works") // 작품 중심의 경로
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 특정 작품의 리뷰 목록 조회 (페이징)
     */
    @GetMapping("/{workId}/reviews")
    public ResponseEntity<CommonResponse<Page<ReviewResponseDto>>> getReviews(
            @PathVariable Long workId,
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page) {

        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        SuccessStatus.READ_SUCCESS.getSuccessCode(),
                        SuccessStatus.READ_SUCCESS.getMessage(),
                        reviewService.findAll(workId, converted)
                )
        );
    }

    /**
     * 리뷰 생성
     */
    @PostMapping("/{workId}/reviews")
    public ResponseEntity<CommonResponse<ReviewResponseDto>> createReview(
            @PathVariable Long workId,
            @Valid @RequestBody ReviewRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.CREATE_SUCCESS,
                        SuccessStatus.CREATE_SUCCESS.getSuccessCode(),
                        SuccessStatus.CREATE_SUCCESS.getMessage(),
                        reviewService.createReview(workId, requestDto, userDetails)
                )
        );
    }
}