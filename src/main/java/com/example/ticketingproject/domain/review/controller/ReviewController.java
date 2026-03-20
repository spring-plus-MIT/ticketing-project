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
@RequestMapping("/works/{workId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<ReviewResponseDto>>> getReviews(
            @PathVariable Long workId,
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page) {

        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        reviewService.findAll(workId, converted)
                )
        );
    }

    @PostMapping
    public ResponseEntity<CommonResponse<ReviewResponseDto>> createReview(
            @PathVariable Long workId,
            @RequestBody @Valid ReviewRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ReviewResponseDto response = reviewService.createReview(workId, requestDto, customUserDetails.getId());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.CREATE_SUCCESS,

                        response
                )
        );
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewResponseDto>> updateReview(
            @PathVariable Long workId,
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ReviewResponseDto response = reviewService.updateReview(workId, reviewId, requestDto, customUserDetails.getId());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.UPDATE_SUCCESS,
                        response
                )
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> deleteReview(
            @PathVariable Long workId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        reviewService.deleteReview(workId, reviewId, customUserDetails.getId());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.DELETE_SUCCESS,
                        null
                )
        );
    }
}
