package com.example.ticketingproject.redis.lock.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.redis.lock.exception.LockException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonLockService {
    private final RedissonClient redissonClient;

    public Object redissonLock(String key, ProceedingJoinPoint joinPoint) throws Throwable {
        // Redis에 key 기반 락 생성
        RLock lock = redissonClient.getLock(key);

        try{
            // 최대 5초 동안 락 대기, 10초 후 자동 해제
            // Redisson 은 Retry 전략을 기본적으로 가지고 있음
            Boolean available = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (!Boolean.TRUE.equals(available)) {
                throw new LockException(
                        ErrorStatus.LOCK_ACQUISITION_FAILED.getHttpStatus(),
                        ErrorStatus.LOCK_ACQUISITION_FAILED
                );
            }

            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new LockException(
                    ErrorStatus.LOCK_INTERRUPTED.getHttpStatus(),
                    ErrorStatus.LOCK_INTERRUPTED
            );
        } finally {
            // 락 검증 후 해제 (다른 쓰레드 락 안풀리게)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
