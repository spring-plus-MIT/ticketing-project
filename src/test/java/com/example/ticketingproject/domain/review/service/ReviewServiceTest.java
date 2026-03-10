package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private User savedUser;
    private Work savedWork;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 미리 생성
        User user = User.builder()
                .email("test@example.com")
                .password("12345678")
                .build();
        savedUser = userRepository.save(user);

        // 테스트용 작품 미리 생성
        Work work = Work.builder()
                .title("테스트 작품")
                .description("테스트 설명")
                .build();
        savedWork = workRepository.save(work);
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_Success() {
        // Given
        String content = "정말 멋진 공연이었어요!";
        Integer rating = 5;

        // When
        Long reviewId = reviewService.createReview(savedUser.getId(), savedWork.getId(), content, rating);

        // Then
        Review result = reviewRepository.findById(reviewId).orElseThrow();
        assertEquals(content, result.getContent());
        assertEquals(rating, result.getRating());
        assertEquals(savedUser.getId(), result.getUser().getId());
    }

    @Test
    @DisplayName("작품별 리뷰 목록 조회 성공")
    void getReviewsByWork_Success() {
        // Given
        reviewService.createReview(savedUser.getId(), savedWork.getId(), "리뷰 1", 5);
        reviewService.createReview(savedUser.getId(), savedWork.getId(), "리뷰 2", 4);

        // When
        List<Review> reviews = reviewService.getReviewsByWork(savedWork.getId());

        // Then
        assertEquals(2, reviews.size());
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_Success() {
        // Given
        Long reviewId = reviewService.createReview(savedUser.getId(), savedWork.getId(), "원래 내용", 3);
        String updatedContent = "수정된 내용입니다.";
        Integer updatedRating = 5;

        // When
        reviewService.updateReview(savedUser.getId(), reviewId, updatedContent, updatedRating);

        // Then
        Review result = reviewRepository.findById(reviewId).orElseThrow();
        assertEquals(updatedContent, result.getContent());
        assertEquals(updatedRating, result.getRating());
    }

    @Test
    @DisplayName("리뷰 삭제(소프트 삭제) 성공")
    void deleteReview_Success() {
        // Given
        Long reviewId = reviewService.createReview(savedUser.getId(), savedWork.getId(), "삭제될 리뷰", 1);

        // When
        reviewService.deleteReview(savedUser.getId(), reviewId);

        // Then
        Review result = reviewRepository.findById(reviewId).orElseThrow();
        assertNotNull(result.getDeletedAt()); // 삭제 시간(deletedAt)이 기록되었는지 확인
    }
}