package com.example.ticketingproject.lock.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LockRedisRepository {
    private final StringRedisTemplate stringRedisTemplate;

    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";

    private static final DefaultRedisScript<Long> SCRIPT =
            new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);

    public Boolean tryLock(String key, String value, Duration ttl) {
        Boolean result = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, value, ttl);

        return Boolean.TRUE.equals(result);
    }

    public void unlock(String key, String value) {
        Long result = stringRedisTemplate.execute(
                SCRIPT,
                Collections.singletonList(key),
                value
        );

        if (result == null || result == 0) {
            log.warn("Lock 해제 실패 key={}, value={}", key, value);
        } else {
            log.info("Lock 해제 성공 key={}, thread={}", key, Thread.currentThread().getName());
        }
    }
}
