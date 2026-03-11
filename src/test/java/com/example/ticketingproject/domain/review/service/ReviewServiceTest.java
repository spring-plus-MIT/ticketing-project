package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.domain.review.dto.request.ReviewRequestDto;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private WorkRepository workRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Work work;
    private Review review;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        user = mock(User.class);
        work = mock(Work.class);
        review = mock(Review.class);
    }

    @Test
    void 리뷰_생성_성공() {

        Long workId = 1L;
        Long userId = 1L;

        ReviewRequestDto request = new ReviewRequestDto();

        ReflectionTestUtils.setField(request, "content", "좋은 공연이었습니다.");
        ReflectionTestUtils.setField(request, "rating", 5);

        given(workRepository.findById(workId)).willReturn(Optional.of(work));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.save(any())).willReturn(review);

        when(review.getUser()).thenReturn(user);
        when(user.getName()).thenReturn("테스트유저");

        ReviewResponseDto response = reviewService.createReview(workId, request, userId);

        verify(workRepository).findById(workId);
        verify(userRepository).findById(userId);
        verify(reviewRepository).save(any());

        assertThat(response).isNotNull();
    }

    @Test
    void 리뷰_생성_실패_작품없음() {

        Long workId = 1L;
        Long userId = 1L;

        ReviewRequestDto request = new ReviewRequestDto();

        given(workRepository.findById(workId)).willReturn(Optional.empty());

        try {
            reviewService.createReview(workId, request, userId);
        } catch (Exception ignored) {}

        verify(userRepository, never()).findById(any());
    }
}
