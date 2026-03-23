package com.example.ticketingproject.common.search.controller;

import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PerformanceSearchIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private WorkRepository workRepository;
    @Autowired private VenueRepository venueRepository;
    @Autowired private PerformanceRepository performanceRepository;
    @Autowired private PerformanceSessionRepository performanceSessionRepository;
    @Autowired private CacheManager cacheManager;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    private String token;

    @BeforeEach
    void setUp() {
        token = createBearerToken(1L, "user@test.com", UserRole.USER);
        Objects.requireNonNull(cacheManager.getCache("performanceSearch")).clear();
    }

    @AfterEach
    void tearDown() {
        performanceSessionRepository.deleteAll();
        performanceRepository.deleteAll();
        venueRepository.deleteAll();
        workRepository.deleteAll();
        Objects.requireNonNull(cacheManager.getCache("performanceSearch")).clear();
    }

    private String createBearerToken(Long id, String email, UserRole role) {
        CustomUserDetails userDetails = new CustomUserDetails(id, email, role);
        return "Bearer " + jwtTokenProvider.createToken(userDetails);
    }

    private Work saveWork(String title, Category category) {
        return workRepository.save(Work.builder()
                .title(title)
                .description(title + " 설명")
                .category(category)
                .likeCount(0L)
                .build());
    }

    private Venue saveVenue() {
        return venueRepository.save(Venue.builder()
                .name("블루스퀘어")
                .address("서울시 용산구")
                .totalSeats(1000)
                .build());
    }

    private Performance savePerformance(Work work, Venue venue, PerformanceStatus status) {
        return performanceRepository.save(Performance.builder()
                .work(work)
                .venue(venue)
                .season("2024 시즌")
                .status(status)
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 8, 31))
                .build());
    }

    private void saveSession(Performance performance, Venue venue, LocalDateTime startTime) {
        performanceSessionRepository.save(PerformanceSession.builder()
                .performance(performance)
                .venue(venue)
                .startTime(startTime)
                .endTime(startTime.plusHours(2))
                .build());
    }

    // ===================== searchV2 =====================

    @Test
    @DisplayName("searchV2 - keyword로 검색 시 해당 공연이 조회된다 - HTTP 200, content 1건")
    void keyword로_공연_검색_성공() throws Exception {
        // given
        Work work = saveWork("레미제라블", Category.MUSICAL);
        Venue venue = saveVenue();
        Performance performance = savePerformance(work, venue, PerformanceStatus.ON_SALE);
        saveSession(performance, venue, LocalDateTime.of(2024, 7, 15, 19, 0));

        // when & then
        mockMvc.perform(get("/performance/search/v2")
                        .header("Authorization", token)
                        .param("keyword", "레미제라블")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].category").value("MUSICAL"))
                .andExpect(jsonPath("$.data.content[0].status").value("ON_SALE"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("searchV2 - 존재하지 않는 keyword로 검색 시 빈 결과 반환 - HTTP 200, content 0건")
    void 존재하지_않는_keyword로_검색_시_빈_결과() throws Exception {
        // given
        Work work = saveWork("레미제라블", Category.MUSICAL);
        Venue venue = saveVenue();
        Performance performance = savePerformance(work, venue, PerformanceStatus.ON_SALE);
        saveSession(performance, venue, LocalDateTime.of(2024, 7, 15, 19, 0));

        // when & then
        mockMvc.perform(get("/performance/search/v2")
                        .header("Authorization", token)
                        .param("keyword", "존재하지않는공연")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @DisplayName("searchV2 - 공연이 없을 때 검색 시 빈 결과 반환 - HTTP 200, content 0건")
    void 데이터_없을_때_검색_시_빈_결과() throws Exception {
        // when & then
        mockMvc.perform(get("/performance/search/v2")
                        .header("Authorization", token)
                        .param("keyword", "레미제라블")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @DisplayName("searchV2 - 세션 시간이 포함된 날짜 범위로 검색 시 공연이 조회된다 - HTTP 200, content 1건")
    void 세션_시간이_포함된_날짜_범위로_검색_성공() throws Exception {
        // given
        Work work = saveWork("레미제라블", Category.MUSICAL);
        Venue venue = saveVenue();
        Performance performance = savePerformance(work, venue, PerformanceStatus.ON_SALE);
        saveSession(performance, venue, LocalDateTime.of(2024, 7, 15, 19, 0));

        // when & then
        mockMvc.perform(get("/performance/search/v2")
                        .header("Authorization", token)
                        .param("startDate", "2024-07-01")
                        .param("endDate", "2024-07-31")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @DisplayName("searchV2 - 세션 시간과 겹치지 않는 날짜 범위로 검색 시 빈 결과 반환 - HTTP 200, content 0건")
    void 세션_시간과_겹치지_않는_날짜_범위로_검색_시_빈_결과() throws Exception {
        // given
        Work work = saveWork("레미제라블", Category.MUSICAL);
        Venue venue = saveVenue();
        Performance performance = savePerformance(work, venue, PerformanceStatus.ON_SALE);
        saveSession(performance, venue, LocalDateTime.of(2024, 7, 15, 19, 0));

        // when & then
        mockMvc.perform(get("/performance/search/v2")
                        .header("Authorization", token)
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-31")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    @DisplayName("searchV2 - ON_SALE 상태 필터 검색 시 ON_SALE 공연만 반환 - HTTP 200, content 1건")
    void ON_SALE_상태_필터_검색_성공() throws Exception {
        // given
        Work work1 = saveWork("레미제라블", Category.MUSICAL);
        Work work2 = saveWork("오페라의유령", Category.MUSICAL);
        Venue venue = saveVenue();
        Performance onSale = savePerformance(work1, venue, PerformanceStatus.ON_SALE);
        Performance closed = savePerformance(work2, venue, PerformanceStatus.CLOSED);
        saveSession(onSale, venue, LocalDateTime.of(2024, 7, 15, 19, 0));
        saveSession(closed, venue, LocalDateTime.of(2024, 7, 20, 19, 0));

        // when & then
        mockMvc.perform(get("/performance/search/v2")
                        .header("Authorization", token)
                        .param("status", "ON_SALE")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].status").value("ON_SALE"));
    }

    @Test
    @DisplayName("searchV2 - 세션이 여러 개인 공연은 중복 없이 1건만 조회된다 - HTTP 200, content 1건")
    void 세션_여러개인_공연_중복_없이_1건_조회() throws Exception {
        // given
        Work work = saveWork("레미제라블", Category.MUSICAL);
        Venue venue = saveVenue();
        Performance performance = savePerformance(work, venue, PerformanceStatus.ON_SALE);
        saveSession(performance, venue, LocalDateTime.of(2024, 7, 15, 14, 0));
        saveSession(performance, venue, LocalDateTime.of(2024, 7, 15, 19, 0));
        saveSession(performance, venue, LocalDateTime.of(2024, 7, 16, 14, 0));

        // when & then - 세션 3개지만 공연은 1건
        mockMvc.perform(get("/performance/search/v2")
                        .header("Authorization", token)
                        .param("keyword", "레미제라블")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
}