package com.example.ticketingproject.domain.review.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.reservation.service.AdminReservationService;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.review.exception.ReviewException;
import com.example.ticketingproject.domain.review.repository.ReviewRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminReviewServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private AdminReservationService adminReservationService;

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

        adminReviewService.deleteReviewByAdmin(reviewId, workId);

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
                adminReviewService.deleteReviewByAdmin(reviewId, workId)
        ).isInstanceOf(ReviewException.class);

        verify(reviewRepository).findById(reviewId);
    }

    @Test
    @DisplayName("관리자 - 전체 예약 목록 조회 테스트")
    void getAllReservationsTest() {
        Work mockWork = Work.builder().build();
        Performance mockPerformance = Performance.builder().work(mockWork).build();
        PerformanceSession mockSession = PerformanceSession.builder().performance(mockPerformance).build();

        SeatGrade mockGrade = SeatGrade.builder().build();
        ReflectionTestUtils.setField(mockGrade, "gradeName", GradeName.values()[0]);
        ReflectionTestUtils.setField(mockGrade, "id", 1L);

        Seat mockSeat = Seat.builder().seatNumber(1).build();
        ReflectionTestUtils.setField(mockSeat, "seatGrade", mockGrade);
        ReflectionTestUtils.setField(mockSeat, "id", 1L);

        User mockUser = User.builder().build();
        ReflectionTestUtils.setField(mockUser, "id", 1L);

        Reservation reservation = Reservation.builder()
                .user(mockUser)
                .performanceSession(mockSession)
                .seat(mockSeat)
                .build();
        ReflectionTestUtils.setField(reservation, "id", 1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation));
        when(reservationRepository.findAll(pageable)).thenReturn(reservationPage);

        Page<ReservationResponse> result = adminReservationService.getAllReservations(pageable);

        assertThat(result).isNotNull();
        verify(reservationRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("관리자 - 특정 유저의 예약 목록 조회 테스트")
    void getReservationsByUserTest() {
        Work mockWork = Work.builder().build();
        Performance mockPerformance = Performance.builder().work(mockWork).build();
        PerformanceSession mockSession = PerformanceSession.builder().performance(mockPerformance).build();

        SeatGrade mockGrade = SeatGrade.builder().build();
        ReflectionTestUtils.setField(mockGrade, "gradeName", GradeName.values()[0]);
        ReflectionTestUtils.setField(mockGrade, "id", 1L);

        Seat mockSeat = Seat.builder().seatNumber(1).build();
        ReflectionTestUtils.setField(mockSeat, "seatGrade", mockGrade);
        ReflectionTestUtils.setField(mockSeat, "id", 1L);

        Long userId = 1L;
        User mockUser = User.builder().build();
        ReflectionTestUtils.setField(mockUser, "id", userId);

        Reservation reservation = Reservation.builder()
                .user(mockUser)
                .performanceSession(mockSession)
                .seat(mockSeat)
                .build();
        ReflectionTestUtils.setField(reservation, "id", 1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation));

        when(reservationRepository.findAllByUserId(userId, pageable)).thenReturn(reservationPage);

        Page<ReservationResponse> result = adminReservationService.getReservationsByUser(userId, pageable);

        assertThat(result).isNotNull();
        verify(reservationRepository, times(1)).findAllByUserId(userId, pageable);
    }
}