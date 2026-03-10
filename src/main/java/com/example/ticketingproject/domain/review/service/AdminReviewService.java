package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 관리자용 전체 리뷰 페이징 조회
     */
    public Page<ReviewResponseDto> findAllReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(pageable);

        return reviews.map(review -> ReviewResponseDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .nickname(review.getUser().getName())
                .createdAt(review.getCreatedAt())
                .build()
        );
    }

    /**
     * 관리자 권한 리뷰 삭제
     * UserService.withdrawUser 예외 처리 스타일 반영
     */
    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException(
                        REVIEW_NOT_FOUND.getHttpStatus(),
                        REVIEW_NOT_FOUND
                )
        );

        reviewRepository.delete(review);
    }
}