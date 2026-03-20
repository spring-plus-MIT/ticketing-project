package com.example.ticketingproject.redis;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seat.service.AdminSeatService;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.exception.VenueException;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class RedisLockAspectTest {

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
    private PerformanceRepository performanceRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Venue venue;
    private SeatGrade seatGrade;

    @BeforeEach
    void setUp() {
        Work work = Work.builder()
                .title("테스트 공연")
                .category(Category.MUSICAL)
                .description("설명")
                .likeCount(0L)
                .build();
        Work savedWork = workRepository.save(work);

        venue = Venue.builder()
                .name("테스트 공연장")
                .address("서울시 강남구")
                .totalSeats(10)
                .build();
        venue = venueRepository.save(venue);

        Performance performance = Performance.builder()
                .work(savedWork)
                .venue(venue)
                .season("시즌1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .status(PerformanceStatus.ON_SALE)
                .build();
        Performance savedPerformance = performanceRepository.save(performance);

        PerformanceSession session = PerformanceSession.builder()
                .performance(savedPerformance)
                .venue(venue)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();
        PerformanceSession savedSession = performanceSessionRepository.save(session);

        seatGrade = SeatGrade.builder()
                .performanceSession(savedSession)
                .gradeName(GradeName.VIP)
                .price(BigDecimal.valueOf(50000))
                .totalSeats(10)
                .remainingSeats(10)
                .build();
        seatGrade = seatGradeRepository.save(seatGrade);
    }

    @AfterEach
    void tearDown() {
        seatRepository.deleteAll();
        seatGradeRepository.deleteAll();
        performanceSessionRepository.deleteAll();
        performanceRepository.deleteAll();
        venueRepository.deleteAll();
        workRepository.deleteAll();
        stringRedisTemplate.delete("lock:venue:" + venue.getId() + ":seat:create");
    }

    @Test
    void adminSeatService_AOP_프록시_적용_확인() {
        // @RedisLock AOP가 실제로 Spring 프록시에 의해 적용되었는지 검증
        assertThat(AopUtils.isAopProxy(adminSeatService)).isTrue();
    }

    @Test
    void saveRedisLock_정상_실행_후_좌석_생성_및_락_해제_확인() {
        // given
        String lockKey = "lock:venue:" + venue.getId() + ":seat:create";

        CreateSeatRequest request = new CreateSeatRequest();
        ReflectionTestUtils.setField(request, "gradeName", GradeName.VIP);
        ReflectionTestUtils.setField(request, "rowName", "A");
        ReflectionTestUtils.setField(request, "seatNumber", 1);

        // when
        SeatResponse response = adminSeatService.saveRedisLock(venue.getId(), request);

        // then: 좌석이 정상 생성되었는지 확인
        assertThat(response).isNotNull();
        assertThat(seatRepository.countByVenueId(venue.getId())).isEqualTo(1);

        // then: 메서드 완료 후 Redis에서 락이 해제되었는지 확인
        assertThat(stringRedisTemplate.hasKey(lockKey)).isFalse();
    }

    @Test
    void saveRedisLock_예외_발생_시에도_락_해제_확인() {
        // given: 존재하지 않는 venueId로 메서드 내부에서 VenueException 유발
        Long nonExistentVenueId = 9999L;
        String lockKey = "lock:venue:" + nonExistentVenueId + ":seat:create";

        CreateSeatRequest request = new CreateSeatRequest();
        ReflectionTestUtils.setField(request, "gradeName", GradeName.VIP);
        ReflectionTestUtils.setField(request, "rowName", "A");
        ReflectionTestUtils.setField(request, "seatNumber", 1);

        // when & then: 락 획득 → 메서드 예외 발생 → 예외 전파
        assertThatThrownBy(() -> adminSeatService.saveRedisLock(nonExistentVenueId, request))
                .isInstanceOf(VenueException.class);

        // then: finally 블록에서 락이 해제되었는지 확인
        assertThat(stringRedisTemplate.hasKey(lockKey)).isFalse();
    }
}
