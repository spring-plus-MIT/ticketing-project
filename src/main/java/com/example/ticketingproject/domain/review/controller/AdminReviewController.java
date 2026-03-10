package com.example.ticketingproject.domain.review.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reviews") // 관리자 전용 경로 분리
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    /**
     * [관리자] 전체 리뷰 목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<Page<ReviewResponseDto>>> getAllReviewsByAdmin(
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page) {

        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        SuccessStatus.READ_SUCCESS.getSuccessCode(),
                        SuccessStatus.READ_SUCCESS.getMessage(),
                        adminReviewService.findAllReviews(converted)
                )
        );
    }

    /**
     * [관리자] 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> deleteReviewByAdmin(@PathVariable Long reviewId) {
        adminReviewService.deleteReviewByAdmin(reviewId);

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.DELETE_SUCCESS,
                        SuccessStatus.DELETE_SUCCESS.getSuccessCode(),
                        SuccessStatus.DELETE_SUCCESS.getMessage(),
                        null
                )
        );
    }
}