package com.example.ticketingproject.domain.review.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.review.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    @GetMapping("/admin/performances/{performanceId}/reviews")
    public ResponseEntity<?> getAllReviews(
            @PathVariable Long performanceId,
            Pageable converted
    ) {
        return ResponseEntity.ok(
                adminReviewService.findAllReviews(performanceId, converted) 
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> deleteReviewByAdmin(@PathVariable Long reviewId) {
        adminReviewService.deleteReviewByAdmin(reviewId);

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.DELETE_SUCCESS,
                        null
                )
        );
    }
}