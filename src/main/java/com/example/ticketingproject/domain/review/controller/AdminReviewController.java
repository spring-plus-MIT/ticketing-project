package com.example.ticketingproject.domain.review.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.service.AdminReviewService;
import com.example.ticketingproject.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.DELETE_SUCCESS;
import static com.example.ticketingproject.common.enums.SuccessStatus.READ_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/works/{workId}/reviews")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<ReviewResponseDto>>> getAllReviews(
            @PathVariable Long workId,
            @PageableDefault Pageable pageable,
            @RequestParam (defaultValue = "1")  int page
    ) {
        Pageable converted= PageRequest.of(page-1,pageable.getPageSize(),pageable.getSort());
        return ResponseEntity.ok(
                CommonResponse.success(
                        READ_SUCCESS,
                        reviewService.findAll(workId, converted)
                )
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> deleteReviewByAdmin(
            @PathVariable Long workId,
            @PathVariable Long reviewId
    ) {
        adminReviewService.deleteReviewByAdmin(reviewId,workId);

        return ResponseEntity.ok(
                CommonResponse.success(
                        DELETE_SUCCESS,
                        null
                )
        );
    }
}