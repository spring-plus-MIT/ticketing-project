package com.example.ticketingproject.domain.payment.service;

import com.example.ticketingproject.auth.exception.AuthException;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.payment.dto.CreatePaymentRequest;
import com.example.ticketingproject.domain.payment.dto.PaymentResponse;
import com.example.ticketingproject.domain.payment.entity.Payment;
import com.example.ticketingproject.domain.payment.enums.PaymentStatus;
import com.example.ticketingproject.domain.payment.exception.PaymentException;
import com.example.ticketingproject.domain.payment.repository.PaymentRepository;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.enums.SeatStatus;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    private Reservation setReservation;

    private User setUser;

    @BeforeEach
    void setUp() {
        // given
        Work work = Work.builder()
                .title("제목")
                .category(Category.MUSICAL)
                .description("설명")
                .likeCount(0L)
                .build();

        ReflectionTestUtils.setField(work, "id", 1L);

        Venue venue = Venue.builder()
                .name("장소")
                .address("주소")
                .totalSeats(300)
                .build();

        ReflectionTestUtils.setField(venue, "id", 1L);

        Performance performance = Performance.builder()
                .work(work)
                .venue(venue)
                .season("세션")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .status(PerformanceStatus.ON_SALE)
                .build();

        ReflectionTestUtils.setField(performance, "id", 1L);

        PerformanceSession session = PerformanceSession.builder()
                .performance(performance)
                .venue(venue)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(3))
                .build();

        ReflectionTestUtils.setField(session, "id", 1L);

        SeatGrade seatGrade = SeatGrade.builder()
                .performanceSession(session)
                .gradeName(GradeName.VIP)
                .price(BigDecimal.valueOf(100))
                .totalSeats(300)
                .remainingSeats(300)
                .build();

        ReflectionTestUtils.setField(seatGrade, "id", 1L);

        Seat seat = Seat.builder()
                .venue(venue)
                .seatGrade(seatGrade)
                .rowName("A")
                .seatNumber(1)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();

        ReflectionTestUtils.setField(seat, "id", 1L);

        User user = User.builder()
                .name("이름")
                .email("test1@test.com")
                .password("12345678")
                .phone("010-1234-1234")
                .balance(BigDecimal.valueOf(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);

        Reservation reservation = Reservation.builder()
                .user(user)
                .performanceSession(session)
                .seat(seat)
                .status(ReservationStatus.PENDING)
                .totalPrice(seat.getSeatGrade().getPrice())
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        ReflectionTestUtils.setField(reservation, "id", 1L);

        setReservation = reservation;
        setUser = user;
    }

    @Test
    void 결제_생성_성공_테스트() {

        // give
        CreatePaymentRequest request = new CreatePaymentRequest();
        ReflectionTestUtils.setField(request, "reservationId", setReservation.getId());
        ReflectionTestUtils.setField(request, "amount", BigDecimal.valueOf(500));
        when(reservationRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(setReservation));

        when(paymentRepository.existsByReservationId(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PaymentResponse response = paymentService.createPayment(request, 1L);

        // then
        assertThat(response.getAmount()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertThat(captor.getValue().getReservation()).isEqualTo(setReservation);

        assertThat(setReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(setReservation.getSeat().getSeatStatus()).isEqualTo(SeatStatus.SOLD);
    }

    @Test
    void 결제_생성_실패_테스트() {
        // given
        CreatePaymentRequest request = new CreatePaymentRequest();
        ReflectionTestUtils.setField(request, "reservationId", setReservation.getId());
        ReflectionTestUtils.setField(request, "amount", BigDecimal.valueOf(500));

        when(reservationRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                paymentService.createPayment(request, 1L)
        ).isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorStatus.ACCESS_FORBIDDEN.getMessage());
    }

    @Test
    void 결제_내역_단건_조회_성공_테스트() {
        // given
        Payment payment = Payment.builder()
                .reservation(setReservation)
                .user(setUser)
                .amount(BigDecimal.valueOf(10000))
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        ReflectionTestUtils.setField(payment, "id", 3L);

        when(paymentRepository.findByIdAndUserId(3L, 1L))
                .thenReturn(Optional.of(payment));

        // when
        PaymentResponse response = paymentService.findOnePayment(3L, 1L);

        // then
        assertThat(response.getAmount()).isEqualTo(BigDecimal.valueOf(10000));
    }

    @Test
    void 결제_내역_단건_조회_실패_테스트() {

        // given
        Payment payment = Payment.builder()
                .reservation(setReservation)
                .user(setUser)
                .amount(BigDecimal.valueOf(10000))
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        ReflectionTestUtils.setField(payment, "id", 4L);

        when(paymentRepository.findByIdAndUserId(4L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                paymentService.findOnePayment(4L, 1L)
        ).isInstanceOf(PaymentException.class)
                .hasMessageContaining(ErrorStatus.PAYMENT_NOT_FOUND.getMessage());
    }

    @Test
    void 결제_내역_목록_조회_성공_테스트() {
        // given
        Payment payment = Payment.builder()
                .reservation(setReservation)
                .user(setUser)
                .amount(BigDecimal.valueOf(15000))
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();
        
        ReflectionTestUtils.setField(payment, "id", 5L);

        when(paymentRepository.findAllByUserId(eq(1L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(payment)));

        // when
        Page<PaymentResponse> responses = paymentService.findAllPayments(1L, PageRequest.of(0, 10));

        // then
        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).getAmount()).isEqualTo(BigDecimal.valueOf(15000));
    }
}
