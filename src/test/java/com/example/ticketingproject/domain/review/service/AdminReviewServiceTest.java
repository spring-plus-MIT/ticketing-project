package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AdminReviewService adminReviewService;

    private Review review;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().name("관리자확인용").build();
        review = Review.builder().content("삭제될 리뷰").rating(1).user(user).build();
        ReflectionTestUtils.setField(review, "id", 1L);
    }

    @Test
    @DisplayName("관리자 전체 리뷰 조회 성공")
    void findAllReviews_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);
        given(reviewRepository.findAll(pageable)).willReturn(reviewPage);

        // when
        Page<ReviewResponseDto> response = adminReviewService.findAllReviews(pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getNickname()).isEqualTo("관리자확인용");
    }

    @Test
    @DisplayName("관리자 리뷰 삭제 성공")
    void delete_success() {
        // given
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        // when
        assertThatNoException().isThrownBy(() -> adminReviewService.deleteReviewByAdmin(1L));

        // then
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    @DisplayName("관리자 리뷰 삭제 실패 - 존재하지 않는 리뷰")
    void delete_fail_reviewNotFound() {
        // given
        given(reviewRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminReviewService.deleteReviewByAdmin(999L))
                .isInstanceOf(ReviewException.class);
    }
}