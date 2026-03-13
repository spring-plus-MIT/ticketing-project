package com.example.ticketingproject.lock.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.lock.exception.LockException;
import com.example.ticketingproject.lock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LockService {
    private final LockRedisRepository lockRedisRepository;

    private static final int RETRY_COUNT = 5;
    private static final long RETRY_DELAY = 100;
    private static final Duration LOCK_TTL = Duration.ofSeconds(10);

    public String lock(String key) {
        String uuid = UUID.randomUUID().toString();

        try{
            for (int i = 0; i < RETRY_COUNT; i++) {
                Boolean success = lockRedisRepository.tryLock(key, uuid, LOCK_TTL);

                if (success) {
                    return uuid;
                }

                Thread.sleep(RETRY_DELAY * (i + 1));
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new LockException(
                    ErrorStatus.LOCK_INTERRUPTED.getHttpStatus(),
                    ErrorStatus.LOCK_INTERRUPTED
            );
        }

        throw new LockException(
                ErrorStatus.LOCK_ACQUISITION_FAILED.getHttpStatus(),
                ErrorStatus.LOCK_ACQUISITION_FAILED
        );
    }

    public void unlock(String key, String uuid) {
        lockRedisRepository.unlock(key, uuid);
    }

    public String createSessionAndSeatLockKey(Long sessionId, Long seatId) {
        return "lock:session:" + sessionId + "seat:" + seatId;
    }
}
