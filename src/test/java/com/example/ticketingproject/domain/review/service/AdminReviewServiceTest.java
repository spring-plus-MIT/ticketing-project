package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private WorkRepository workRepository;

    @InjectMocks
    private AdminReviewService adminReviewService;

    @Test
    void 관리자_리뷰_전체조회_성공() {

        Long workId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Review review = mock(Review.class);
        User user = mock(User.class);

        when(review.getUser()).thenReturn(user);
        when(user.getName()).thenReturn("테스트유저");

        Page<Review> reviewPage =
                new PageImpl<>(List.of(review));

        when(reviewRepository.findAllByWorkId(workId, pageable))
                .thenReturn(reviewPage);

        Page<ReviewResponseDto> result =
                adminReviewService.findAllReviews(workId, pageable);

        verify(reviewRepository).findAllByWorkId(workId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(1);
    }

    @Test
    void 관리자_리뷰삭제_성공() {

        Long reviewId = 1L;
        Long workId = 1L;
        Review review = mock(Review.class);
        Work work = mock(Work.class);

        when(workRepository.findById(workId))
                .thenReturn(Optional.of(work));
        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(review));

        adminReviewService.deleteReviewByAdmin(reviewId,workId);

        verify(reviewRepository).findById(reviewId);
        verify(review).delete();
    }

    @Test
    void 관리자_리뷰삭제_실패_리뷰없음() {

        Long reviewId = 1L;
        Long workId = 1L;
        Work work = mock(Work.class);
        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.empty());
        when(workRepository.findById(workId))
                .thenReturn(Optional.of(work));
        assertThatThrownBy(() ->
                adminReviewService.deleteReviewByAdmin(reviewId,workId)
        ).isInstanceOf(ReviewException.class);

        verify(reviewRepository).findById(reviewId);
    }
}
