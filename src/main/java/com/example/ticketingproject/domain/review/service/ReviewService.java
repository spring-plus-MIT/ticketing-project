package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final WorkRepository workRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createReview(Long userId, Long workId, String content, Integer rating) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("작품을 찾을 수 없습니다."));

        Review review = Review.builder()
                .user(user)
                .work(work)
                .content(content)
                .rating(rating)
                .build();

        return reviewRepository.save(review).getId();
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByWork(Long workId) {
        return reviewRepository.findAllByWorkIdAndDeletedAtIsNullOrderByCreatedAtDesc(workId);
    }

    @Transactional
    public void updateReview(Long userId, Long reviewId, String content, Integer rating) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        review.update(content, rating, userId);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        review.validateOwner(userId);
        review.delete();
    }
}