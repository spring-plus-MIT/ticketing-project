package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.REVIEW_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewService {

    private final ReviewRepository reviewRepository;

    public Page<ReviewResponseDto> findAllReviews(Long workId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByWorkId(workId, pageable);
        return reviews.map(ReviewResponseDto::from); // ReviewResponseDto.from() 사용
    }


    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(
                        REVIEW_NOT_FOUND.getHttpStatus(),
                        REVIEW_NOT_FOUND
                ));

        review.delete();
    }
}
