package com.example.ticketingproject.redis;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.service.AdminSeatService;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import com.example.ticketingproject.redis.lock.service.LockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class RedisLockAspectTest {

    @Autowired
    private AdminSeatService adminSeatService;

    @SpyBean
    private LockService lockService;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PerformanceSessionRepository performanceSessionRepository;

    @Autowired
    private SeatGradeRepository seatGradeRepository;

    private Venue venue;

    private SeatGrade seatGrade;

    @AfterEach
    void tearDown() {
        seatGradeRepository.deleteAll();
        performanceSessionRepository.deleteAll();
        performanceRepository.deleteAll();
        venueRepository.deleteAll();
        workRepository.deleteAll();
    }

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
                .totalSeats(20)
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
                .gradeName(GradeName.R)
                .price(BigDecimal.valueOf(100))
                .totalSeats(20)
                .remainingSeats(20)
                .build();

        SeatGrade savedSeatGrade = seatGradeRepository.save(seatGrade);
    }

    @Test
    void RedisLock_어노테이션_성공_테스트() {

        // given

        CreateSeatRequest request = mock(CreateSeatRequest.class);
        given(request.getGradeName()).willReturn(GradeName.R);
        given(request.getRowName()).willReturn("A");
        given(request.getSeatNumber()).willReturn(1);

        // when
        adminSeatService.saveRedisLock(venue.getId(), request);

        // then
        verify(lockService).lockRetry(any());
        verify(lockService).unlock(any(), any());
    }
}
