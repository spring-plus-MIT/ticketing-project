package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
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
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private SeatGradeRepository seatGradeRepository;
    @Autowired private PerformanceSessionRepository performanceSessionRepository;
    @Autowired private PerformanceRepository performanceRepository;
    @Autowired private WorkRepository workRepository;
    @Autowired private VenueRepository venueRepository;

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        seatGradeRepository.deleteAll();
        performanceSessionRepository.deleteAll();
        performanceRepository.deleteAll();
        workRepository.deleteAll();
        userRepository.deleteAll();
        venueRepository.deleteAll();
    }

    private String createAdminBearerToken() {
        CustomUserDetails userDetails = new CustomUserDetails(999L, "admin@test.com", UserRole.ADMIN);
        return "Bearer " + jwtTokenProvider.createToken(userDetails);
    }

    private User saveUser(String email) {
        return userRepository.save(User.builder()
                .name("테스트유저")
                .email(email)
                .password("encodedPassword")
                .phone("010-1234-5678")
                .balance(BigDecimal.ZERO)
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build());
    }

    /**
     * Reservation 생성에 필요한 전체 엔티티 체인 (Work → Venue → Performance → PerformanceSession → SeatGrade → Seat)을 저장하고
     * 주어진 유저에 연결된 Reservation을 반환한다.
     */
    private Reservation saveReservation(User user, int seatNumber) {
        Work work = workRepository.save(Work.builder()
                .title("테스트 공연")
                .category(Category.MUSICAL)
                .description("테스트 공연 설명")
                .likeCount(0L)
                .build());

        Venue venue = venueRepository.save(Venue.builder()
                .name("테스트 공연장")
                .address("서울시 강남구")
                .totalSeats(100)
                .build());

        Performance performance = performanceRepository.save(Performance.builder()
                .work(work)
                .venue(venue)
                .season("2024 시즌")
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .status(PerformanceStatus.ON_SALE)
                .build());

        PerformanceSession session = performanceSessionRepository.save(PerformanceSession.builder()
                .performance(performance)
                .venue(venue)
                .startTime(LocalDateTime.of(2024, 6, 15, 19, 0))
                .endTime(LocalDateTime.of(2024, 6, 15, 21, 0))
                .build());

        SeatGrade seatGrade = seatGradeRepository.save(SeatGrade.builder()
                .performanceSession(session)
                .gradeName(GradeName.VIP)
                .price(BigDecimal.valueOf(100000))
                .totalSeats(10)
                .remainingSeats(10)
                .build());

        Seat seat = seatRepository.save(Seat.builder()
                .venue(venue)
                .seatGrade(seatGrade)
                .rowName("A")
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.RESERVED)
                .build());

        return reservationRepository.save(Reservation.builder()
                .user(user)
                .performanceSession(session)
                .seat(seat)
                .status(ReservationStatus.PENDING)
                .totalPrice(BigDecimal.valueOf(100000))
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build());
    }

    // ===================== 고객 예약 불러오기 =====================

    @Test
    @DisplayName("고객 예약 불러오기 성공 - HTTP 200, code 200_READ_SUCCESS, 총 예약 내역 3개 반환")
    void 고객_예약_불러오기_성공() throws Exception {
        // given
        User user = saveUser("user@test.com");
        saveReservation(user, 1);
        saveReservation(user, 2);
        saveReservation(user, 3);
        String token = createAdminBearerToken();

        // when & then
        mockMvc.perform(get("/admin/reservations/users/" + user.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.content.length()").value(3));
    }

    @Test
    @DisplayName("고객 예약 불러오기 실패 - 존재하지 않는 고객 ID로 요청 시 빈 페이지 반환")
    void 고객_예약_불러오기_실패_존재하지_않는_고객() throws Exception {
        // given
        String token = createAdminBearerToken();
        long nonExistentUserId = 99999L;

        // when & then
        mockMvc.perform(get("/admin/reservations/users/" + nonExistentUserId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.content").isEmpty());
    }
}
