package com.example.ticketingproject.domain.reservation.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.enums.SeatStatus;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.*;

@SpringBootTest
public class ReservationThreadsTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatGradeRepository seatGradeRepository;

    @Autowired
    private PerformanceSessionRepository performanceSessionRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Long performanceSessionId;
    private Long seatId;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private WorkRepository workRepository;

    @BeforeEach
    void setUp() {
        // given
        Work work = Work.builder()
                .title("제목")
                .category(Category.MUSICAL)
                .description("설명")
                .likeCount(0L)
                .build();

        Work savedWork = workRepository.save(work);

        Venue venue = Venue.builder()
                .name("장소")
                .address("주소")
                .totalSeats(300)
                .build();

        Venue savedVenue = venueRepository.save(venue);

        Performance performance = Performance.builder()
                .work(savedWork)
                .venue(savedVenue)
                .season("세션")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .status(PerformanceStatus.ON_SALE)
                .build();

        Performance savedPerformance = performanceRepository.save(performance);

        PerformanceSession session = PerformanceSession.builder()
                .performance(savedPerformance)
                .venue(savedVenue)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(3))
                .build();

        PerformanceSession savedSession = performanceSessionRepository.save(session);

        SeatGrade seatGrade = SeatGrade.builder()
                .performanceSession(savedSession)
                .gradeName(GradeName.VIP)
                .price(BigDecimal.valueOf(100))
                .totalSeats(300)
                .remainingSeats(300)
                .build();

        SeatGrade savedSeatGrade = seatGradeRepository.save(seatGrade);

        Seat seat = Seat.builder()
                .venue(savedVenue)
                .seatGrade(savedSeatGrade)
                .rowName("A")
                .seatNumber(1)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();

        Seat savedSeat = seatRepository.save(seat);

        performanceSessionId = savedSession.getId();
        seatId = savedSeat.getId();
    }

//    @Test
//    void 동시에_100명이_1개의_좌석을_예약하면_100명_성공_테스트() throws InterruptedException, BrokenBarrierException {
//
//        // given
//        int threadCount = 100;
//
//        // 멀티 스레드를 만들어서 요청을 실행
//        ExecutorService executorService = Executors.newFixedThreadPool(100);
//
//        // 모든 스레드가 완료될 때까지 대기
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        // 최대한 동시 실행을 위한 cyclicBarrier 추가
//        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount + 1);
//
//        // when
//        for (int i = 0; i < threadCount; i++) {
//
//            int finalI = i;
//
//            // 100개의 스레드가 동시에 reservationService.createReservation() 호출
//            executorService.submit(() -> {
//                try {
//                    // 모든 스레드가 만들어 질 때까지 대기
//                    cyclicBarrier.await();
//
//                    User user = User.builder()
//                            .name("이름")
//                            .email("test"+finalI+"@test.com")
//                            .password("12345678")
//                            .phone("010-1234-1234")
//                            .balance(BigDecimal.valueOf(1000))
//                            .userRole(UserRole.USER)
//                            .userStatus(UserStatus.ACTIVE)
//                            .build();
//
//                    userRepository.saveAndFlush(user);
//
//                    ReservationCreateRequest request = new ReservationCreateRequest();
//
//                    ReflectionTestUtils.setField(request, "performanceSessionId", performanceSessionId);
//                    ReflectionTestUtils.setField(request, "seatId", seatId);
//
//                    reservationService.createReservation(request, user.getId());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        // 메인 스레드도 대기 처리
//        cyclicBarrier.await();
//
//        latch.await();
//
//        executorService.shutdown();
//
//        // then
//        long reservationCount = reservationRepository.count();
//
//        System.out.println("예약 성공 수 : " + reservationCount);
//
//        Assertions.assertTrue(reservationCount > 1);
//    }
}