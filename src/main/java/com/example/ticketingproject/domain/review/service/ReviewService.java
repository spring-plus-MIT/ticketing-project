package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.review.dto.request.ReviewRequestDto;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final WorkRepository workRepository;
    private final UserRepository userRepository;

    /**
     * 리뷰 목록 조회
     */
    public Page<ReviewResponseDto> findAll(Long workId, Pageable pageable) {

        Page<Review> reviews =
                reviewRepository.findAllByWorkId(workId, pageable);


        return reviews.map(ReviewResponseDto::from);
    }

    @Transactional
    public ReviewResponseDto createReview(
            Long workId,
            ReviewRequestDto requestDto,
            Long userId
    ) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new WorkException(
                        ErrorStatus.WORK_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.WORK_NOT_FOUND
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                ));

        Review review = Review.builder()
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .user(user)
                .work(work)
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDto.from(savedReview);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long workId, Long reviewId, ReviewRequestDto requestDto, Long userId) {
        workRepository.findById(workId)
                .orElseThrow(() -> new WorkException(
                        ErrorStatus.WORK_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.WORK_NOT_FOUND
                ));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(
                        ErrorStatus.REVIEW_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.REVIEW_NOT_FOUND
                ));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                ));
        review.update(requestDto.getContent(), requestDto.getRating());

        return ReviewResponseDto.from(review);
    }

    @Transactional
    public void deleteReview(Long workId, Long reviewId, Long userId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new WorkException(
                        ErrorStatus.WORK_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.WORK_NOT_FOUND
                ));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(
                        ErrorStatus.REVIEW_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.REVIEW_NOT_FOUND
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                ));
        review.delete();
    }
}
