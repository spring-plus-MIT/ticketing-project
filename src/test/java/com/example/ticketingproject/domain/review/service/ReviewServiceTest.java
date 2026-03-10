package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.domain.review.dto.request.ReviewRequestDto;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import com.example.ticketingproject.security.CustomUserDetails;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private WorkRepository workRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    // 필드 선언 완료 - 이제 setUp()에서 할당해도 에러 안 납니다!
    private User user;
    private Work work;
    private Review review;

    @BeforeEach
    void setUp() {
        user = User.builder().name("김규범").build();
        ReflectionTestUtils.setField(user, "id", 1L);

        work = Work.builder().title("테스트 공연").build();
        ReflectionTestUtils.setField(work, "id", 1L);

        review = Review.builder()
                .content("최고의 공연입니다!")
                .rating(5)
                .user(user)
                .work(work)
                .build();
        ReflectionTestUtils.setField(review, "id", 1L);
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {

        //given
        User user= User.builder()
                .name("이름")
                .email("test1@test.com")
                .password("12345678")
                .phone("010-1234-1234")
                .balance(BigDecimal.valueOf(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        ReviewRequestDto requestDto = new ReviewRequestDto();
        ReflectionTestUtils.setField(requestDto,"content","최고의 공연입니다!");
        ReflectionTestUtils.setField(requestDto,"rating",5);

        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(reviewRepository.save(any(Review.class))).willReturn(review);

        // when
        ReviewResponseDto response = reviewService.createReview(1L, requestDto, user.getId());

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo("최고의 공연입니다!");
        assertThat(response.getUserName()).isEqualTo("김규범");
        assertThat(response.getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 존재하지 않는 작품")
    void createReview_fail_workNotFound() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto();
        ReflectionTestUtils.setField(requestDto,"content","최고의 공연입니다!");
        ReflectionTestUtils.setField(requestDto,"rating",5);
        User user= User.builder()
                .name("이름")
                .email("test1@test.com")
                .password("12345678")
                .phone("010-1234-1234")
                .balance(BigDecimal.valueOf(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        given(workRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(999L, requestDto, user.getId()))
                .isInstanceOf(ReviewException.class);
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공")
    void findAll_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        // ReviewRepository 인터페이스에 이 메서드가 정확히 있어야 합니다.
        given(reviewRepository.findAllByWorkId(1L, pageable)).willReturn(reviewPage);

        // when
        Page<ReviewResponseDto> response = reviewService.findAll(1L, pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getContent()).isEqualTo("최고의 공연입니다!");
    }
}