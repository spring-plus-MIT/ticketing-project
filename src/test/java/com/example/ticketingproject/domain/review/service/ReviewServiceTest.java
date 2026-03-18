package com.example.ticketingproject.domain.review.service;
import com.example.ticketingproject.domain.review.dto.request.ReviewRequestDto;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

        assertThat(response).isNotNull();
        verify(reviewRepository).save(any());
    }

    @Test
    void 리뷰_생성_실패_작품없음() {
        Long workId = 1L;
        Long userId = 1L;
        ReviewRequestDto request = new ReviewRequestDto();

        given(workRepository.findById(workId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(workId, request, userId))
                .isInstanceOf(Exception.class);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void 리뷰_수정_성공() {
        Long workId = 1L;
        Long reviewId = 1L;
        Long userId = 1L;
        ReviewRequestDto request = new ReviewRequestDto();
        ReflectionTestUtils.setField(request, "content", "수정된 내용");
        ReflectionTestUtils.setField(request, "rating", 4);

        given(workRepository.findById(workId)).willReturn(Optional.of(work));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));


        given(review.getUser()).willReturn(user);
        given(user.getId()).willReturn(userId);

        ReviewResponseDto response = reviewService.updateReview(workId, reviewId, request, userId);

        verify(review).update(anyString(), anyInt());
        assertThat(response).isNotNull();
    }


    @Test
    void 리뷰_수정_실패_권한없음() {
        Long workId = 1L;
        Long reviewId = 1L;
        Long loginUserId = 1L;
        Long authorId = 99L; // 작성자는 99번

        User author = mock(User.class);
        User loginUser = mock(User.class);

        given(workRepository.findById(workId)).willReturn(Optional.of(work));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(userRepository.findById(loginUserId)).willReturn(Optional.of(loginUser));


        given(review.getUser()).willReturn(author);
        given(author.getId()).willReturn(authorId);

        assertThatThrownBy(() -> reviewService.updateReview(workId, reviewId, new ReviewRequestDto(), loginUserId))
                .isInstanceOf(ReviewException.class)
                .hasMessageContaining("리뷰가 존재하지 않습니다.");
    }

    @Test
    void 리뷰_삭제_성공() {
        Long workId = 1L;
        Long reviewId = 1L;
        Long userId = 1L;

        given(workRepository.findById(workId)).willReturn(Optional.of(work));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(review.getUser()).willReturn(user);
        given(user.getId()).willReturn(userId);

        reviewService.deleteReview(workId, reviewId, userId);

        verify(review).delete();
    }

    @Test
    void 리뷰_목록_조회_성공() {
        Long workId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review));

        given(reviewRepository.findAllByWorkId(workId, pageable)).willReturn(reviewPage);
        given(review.getUser()).willReturn(user);
        given(user.getName()).willReturn("테스트유저");

        Page<ReviewResponseDto> response = reviewService.findAll(workId, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        verify(reviewRepository).findAllByWorkId(workId, pageable);
    }
}