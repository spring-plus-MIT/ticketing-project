package com.example.ticketingproject.redis.lock.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.redis.lock.exception.LockException;
import com.example.ticketingproject.redis.lock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

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

        while (retry > 0) {
            Boolean success = lockRedisRepository.tryLock(key, uuid, LOCK_TTL);

            if (Boolean.TRUE.equals(success)) {
                return uuid;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            retry--;
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
        return "lock:session:" + sessionId + ":seat:" + seatId;
    }

    public String createVenueAndSeatLockKey(Long venueId){
        return "lock:venue:" + venueId + ":seat:create";
    }
}
