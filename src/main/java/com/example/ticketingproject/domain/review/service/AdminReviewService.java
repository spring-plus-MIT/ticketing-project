package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.REVIEW_NOT_FOUND;
import static com.example.ticketingproject.common.enums.ErrorStatus.WORK_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final WorkRepository workRepository;

    public Page<ReviewResponseDto> findAllReviews(Long workId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByWorkId(workId, pageable);
        return reviews.map(ReviewResponseDto::from);
    }


    @Transactional
    public void deleteReviewByAdmin(Long reviewId, Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(()->new WorkException
                        (WORK_NOT_FOUND.getHttpStatus(),WORK_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(
                        REVIEW_NOT_FOUND.getHttpStatus(),
                        REVIEW_NOT_FOUND
                ));

        review.delete();
    }
}
