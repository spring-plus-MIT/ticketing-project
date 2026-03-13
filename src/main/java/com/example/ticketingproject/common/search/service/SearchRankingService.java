package com.example.ticketingproject.common.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchRankingService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String POPULAR_SEARCH_KEY = "popular:search:";
    private static final int TOP_N = 10;

    public void recordKeyword(String keyword, Long userId, String domain) {
        if(!StringUtils.hasText(keyword)) return ;

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        String realTimeKey = POPULAR_SEARCH_KEY + domain + now;

        String realTimeDedupKey = "search:dedup:" + domain + ":" + now + ":" + userId +  ":" + keyword;
        Boolean isNewRealtime = stringRedisTemplate.opsForValue()
                .setIfAbsent(realTimeDedupKey, "1", Duration.ofHours(1));

        if (Boolean.TRUE.equals(isNewRealtime)) {
            stringRedisTemplate.opsForZSet().incrementScore(realTimeKey, keyword, 1);
            stringRedisTemplate.expire(realTimeKey, Duration.ofHours(1));
        }
    }

    public List<String> getRealTimeKeywords() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        return getKeywords(POPULAR_SEARCH_KEY + now);
    }

    private List<String> getKeywords(String key) {
        Set<String> keywords = stringRedisTemplate.opsForZSet().reverseRange(key, 0, TOP_N - 1);
        return keywords != null ? new ArrayList<>(keywords) : Collections.emptyList();
    }
}
