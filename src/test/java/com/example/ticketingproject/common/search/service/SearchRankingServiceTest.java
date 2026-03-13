package com.example.ticketingproject.common.search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.*;


    @ExtendWith(MockitoExtension.class)
    class SearchRankingServiceTest {

        @Mock
        private StringRedisTemplate stringRedisTemplate;

        @Mock
        private ValueOperations<String, String> valueOperations;

        @Mock
        private ZSetOperations<String, String> zSetOperations;

        @InjectMocks
        private SearchRankingService searchRankingService;

        @Test
        @DisplayName("인기 검색어 기록 성공 - 신규 검색어")
        void recordKeyword_success_newKeyword() {
            // given
            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
            given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
            given(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .willReturn(true);

            // when
            searchRankingService.recordKeyword("레미제라블", 1L, "performance");

            // then - incrementScore 호출됐는지 검증
            then(zSetOperations).should().incrementScore(anyString(), eq("레미제라블"), eq(1.0));
            then(stringRedisTemplate).should().expire(anyString(), eq(Duration.ofHours(1)));
        }

        @Test
        @DisplayName("인기 검색어 기록 스킵 - 중복 검색어")
        void recordKeyword_skip_duplicateKeyword() {
            // given
            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .willReturn(false);

            // when
            searchRankingService.recordKeyword("레미제라블", 1L, "performance");

            // then
            then(stringRedisTemplate).should(never()).opsForZSet();
        }

        @Test
        @DisplayName("인기 검색어 기록 스킵 - 빈 키워드")
        void recordKeyword_skip_emptyKeyword() {
            // when
            searchRankingService.recordKeyword("", 1L, "performance");

            // then - opsForValue 자체가 호출 안 됨
            then(stringRedisTemplate).should(never()).opsForValue();
            then(stringRedisTemplate).should(never()).opsForZSet();
        }

        @Test
        @DisplayName("실시간 인기 검색어 조회 성공")
        void getRealTimeKeywords_success() {
            // given
            Set<String> mockKeywords = new LinkedHashSet<>(List.of("레미제라블", "오페라의유령", "시카고"));
            given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
            given(zSetOperations.reverseRange(anyString(), eq(0L), eq(9L)))
                    .willReturn(mockKeywords);

            // when
            List<String> result = searchRankingService.getRealTimeKeywords();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0)).isEqualTo("레미제라블");
            assertThat(result.get(1)).isEqualTo("오페라의유령");
        }

        @Test
        @DisplayName("실시간 인기 검색어 조회 성공 - 결과 없음")
        void getRealTimeKeywords_success_empty() {
            // given
            given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
            given(zSetOperations.reverseRange(anyString(), eq(0L), eq(9L)))
                    .willReturn(null);

            // when
            List<String> result = searchRankingService.getRealTimeKeywords();

            // then
            assertThat(result).isEmpty();
        }
    }
