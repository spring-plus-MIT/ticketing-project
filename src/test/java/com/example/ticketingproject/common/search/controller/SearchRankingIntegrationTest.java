package com.example.ticketingproject.common.search.controller;

import com.example.ticketingproject.common.search.service.SearchRankingService;
import com.example.ticketingproject.domain.user.enums.UserRole;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SearchRankingIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private StringRedisTemplate stringRedisTemplate;
    @Autowired private CacheManager cacheManager;
    @Autowired private SearchRankingService searchRankingService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    private String token;

    @BeforeEach
    void setUp() {
        token = createBearerToken(1L, "user@test.com", UserRole.USER);

        Objects.requireNonNull(cacheManager.getCache("popularKeywords")).clear();
        Set<String> searchKeys = stringRedisTemplate.keys("popular:search:*");
        if (searchKeys != null) stringRedisTemplate.delete(searchKeys);
        Set<String> dedupKeys = stringRedisTemplate.keys("search:dedup:*");
        if (dedupKeys != null) stringRedisTemplate.delete(dedupKeys);
    }

    @AfterEach
    void tearDown() {
        Objects.requireNonNull(cacheManager.getCache("popularKeywords")).clear();
        Set<String> searchKeys = stringRedisTemplate.keys("popular:search:*");
        if (searchKeys != null) stringRedisTemplate.delete(searchKeys);
        Set<String> dedupKeys = stringRedisTemplate.keys("search:dedup:*");
        if (dedupKeys != null) stringRedisTemplate.delete(dedupKeys);
    }

    private String createBearerToken(Long id, String email, UserRole role) {
        CustomUserDetails userDetails = new CustomUserDetails(id, email, role);
        return "Bearer " + jwtTokenProvider.createToken(userDetails);
    }

    // ===================== getPopularKeyWords =====================

    @Test
    @DisplayName("getPopularKeyWords - 검색어 기록 후 인기 검색어 목록에 포함된다 - HTTP 200")
    void 검색어_기록_후_인기_검색어_목록에_포함() throws Exception {
        // given
        searchRankingService.recordKeyword("레미제라블", 1L, "performance");
        searchRankingService.recordKeyword("오페라의유령", 2L, "performance");

        // when & then
        mockMvc.perform(get("/search/popular")
                        .header("Authorization", token)
                        .param("domain", "performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasItem("레미제라블")))
                .andExpect(jsonPath("$.data", hasItem("오페라의유령")));
    }

    @Test
    @DisplayName("getPopularKeyWords - 같은 사용자의 중복 검색은 1회만 카운트된다 - HTTP 200")
    void 같은_사용자_중복_검색_1회만_카운트() throws Exception {
        // given - 유저 1이 같은 키워드 3번 검색 → 1회만 카운트
        searchRankingService.recordKeyword("레미제라블", 1L, "performance");
        searchRankingService.recordKeyword("레미제라블", 1L, "performance");
        searchRankingService.recordKeyword("레미제라블", 1L, "performance");

        // 유저 2도 같은 키워드 검색 → 레미제라블 총 2점
        searchRankingService.recordKeyword("레미제라블", 2L, "performance");

        // 유저 3이 다른 키워드 검색 → 오페라의유령 1점
        searchRankingService.recordKeyword("오페라의유령", 3L, "performance");

        // when & then - 레미제라블(2점)이 오페라의유령(1점)보다 상위
        mockMvc.perform(get("/search/popular")
                        .header("Authorization", token)
                        .param("domain", "performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("레미제라블"))
                .andExpect(jsonPath("$.data[1]").value("오페라의유령"));
    }

    @Test
    @DisplayName("getPopularKeyWords - 검색 기록이 없으면 빈 목록 반환 - HTTP 200, data 0건")
    void 검색_기록_없으면_빈_목록_반환() throws Exception {
        // when & then
        mockMvc.perform(get("/search/popular")
                        .header("Authorization", token)
                        .param("domain", "performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("getPopularKeyWords - 검색 횟수가 많은 키워드가 상위에 위치한다 - HTTP 200")
    void 검색_횟수_많은_키워드가_상위에_위치() throws Exception {
        // given - 오페라의유령: 1명, 레미제라블: 3명
        searchRankingService.recordKeyword("오페라의유령", 1L, "performance");
        searchRankingService.recordKeyword("레미제라블", 2L, "performance");
        searchRankingService.recordKeyword("레미제라블", 3L, "performance");
        searchRankingService.recordKeyword("레미제라블", 4L, "performance");

        // when & then
        mockMvc.perform(get("/search/popular")
                        .header("Authorization", token)
                        .param("domain", "performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("레미제라블"))
                .andExpect(jsonPath("$.data[1]").value("오페라의유령"));
    }

    @Test
    @DisplayName("getPopularKeyWords - domain 파라미터 기본값은 performance이다 - HTTP 200")
    void domain_파라미터_기본값은_performance() throws Exception {
        // given
        searchRankingService.recordKeyword("레미제라블", 1L, "performance");

        // when & then - domain 파라미터 없이 호출
        mockMvc.perform(get("/search/popular")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasItem("레미제라블")));
    }
}