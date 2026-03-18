package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SeatRaceConditionRedissonLockTest {

    @Autowired
    private AdminSeatService adminSeatService;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatGradeRepository seatGradeRepository;

    @Autowired
    private PerformanceSessionRepository performanceSessionRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    private Venue venue;

    private SeatGrade seatGrade;

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

        venue = Venue.builder()
                .name("장소")
                .address("주소")
                .totalSeats(1)
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

        seatGrade = SeatGrade.builder()
                .performanceSession(savedSession)
                .gradeName(GradeName.VIP)
                .price(BigDecimal.valueOf(100))
                .totalSeats(1)
                .remainingSeats(1)
                .build();

        SeatGrade savedSeatGrade = seatGradeRepository.save(seatGrade);
    }

    @Test
    void RedisLock_제한_좌석_1개_동시_10개_생성_시_동시성_제한_생성_테스트() throws InterruptedException {
        // given
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            int seatNumber = i;

            executorService.submit(() -> {
                try {
                    cyclicBarrier.await();

                    CreateSeatRequest request = new CreateSeatRequest();
                    ReflectionTestUtils.setField(request, "gradeName", seatGrade.getGradeName());
                    ReflectionTestUtils.setField(request, "rowName", "A");
                    ReflectionTestUtils.setField(request, "seatNumber", seatNumber);

                    adminSeatService.saveRedissonLock(venue.getId(), request);

                } catch (Exception ignored) {

                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        executorService.shutdown();

        // then
        int seatCount = seatRepository.countByVenueId(venue.getId());

        System.out.println("제한된 좌석 수 = " + venue.getTotalSeats());
        System.out.println("생성된 좌석 수 = " + seatCount);

        Assertions.assertEquals(1, seatCount);
    }
}
