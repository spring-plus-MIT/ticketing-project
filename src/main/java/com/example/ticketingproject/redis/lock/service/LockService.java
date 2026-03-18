package com.example.ticketingproject.redis.lock.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.redis.lock.exception.LockException;
import com.example.ticketingproject.redis.lock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class LockService {
    private final LockRedisRepository lockRedisRepository;

    private static final Duration LOCK_TTL = Duration.ofSeconds(10);

    public String lockFailFast(String key) {
        String uuid = UUID.randomUUID().toString();

        Boolean success = lockRedisRepository.tryLock(key, uuid, LOCK_TTL);

        if (!success) {
            throw new LockException(
                    ErrorStatus.LOCK_ACQUISITION_FAILED.getHttpStatus(),
                    ErrorStatus.LOCK_ACQUISITION_FAILED
            );
        }

        return uuid;
    }

    public String lockRetry(String key) {
        String uuid = UUID.randomUUID().toString();

        int retry = 10;

        // Fixed Backoff -> Exponential (지수적으로 점점 증가) + Jitter (랜덤) 방식으로 수정
        int baseDelay = 10; // 기본 대기 시간
        int attempt = 0; // 재시도 횟수

        try {
            while (retry > 0) {
                Boolean success = lockRedisRepository.tryLock(key, uuid, LOCK_TTL);

                if (Boolean.TRUE.equals(success)) {
                    return uuid;
                }

                try {
                    // Exponential Backoff 공식 delay = baseDelay * 2^attempt(2의 재시도 횟수만큼 제곱)
                    long delay = baseDelay * (1L << attempt); // '<<' 비트 시프트 연산자 (2의 attempt 제곱)
                    long jitter = ThreadLocalRandom.current().nextLong(10); // 0~9ms 랜덤 시간 추가

                    Thread.sleep(delay + jitter); // 재시도 전 대기 시간 수정 // 기존 10 = Fixed Backoff
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new LockException(
                            ErrorStatus.LOCK_INTERRUPTED.getHttpStatus(),
                            ErrorStatus.LOCK_INTERRUPTED
                    );
                }

                retry--;

                attempt++;
            }

            throw new LockException(
                    ErrorStatus.LOCK_TIMEOUT.getHttpStatus(),
                    ErrorStatus.LOCK_TIMEOUT
            );

        } catch (LockException e) {
            throw e;

        } catch (Exception e) {
            throw new LockException(
                    ErrorStatus.LOCK_ACQUISITION_FAILED.getHttpStatus(),
                    ErrorStatus.LOCK_ACQUISITION_FAILED
            );
        }
    }

    public void unlock(String key, String uuid) {

        try {

            Boolean succuss = lockRedisRepository.unlock(key, uuid);

            if (Boolean.FALSE.equals(succuss)) {
                throw new LockException(
                        ErrorStatus.LOCK_RELEASE_FAILED.getHttpStatus(),
                        ErrorStatus.LOCK_RELEASE_FAILED
                );
            }

        } catch (LockException e) {
            throw e;

        } catch (Exception e) {
            throw new LockException(
                    ErrorStatus.LOCK_RELEASE_FAILED.getHttpStatus(),
                    ErrorStatus.LOCK_RELEASE_FAILED
            );
        }
    }
}
