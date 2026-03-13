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

        Boolean success = lockRedisRepository.tryLock(key, uuid, LOCK_TTL);

        if (!success) {
            throw new LockException(
                    ErrorStatus.LOCK_ACQUISITION_FAILED.getHttpStatus(),
                    ErrorStatus.LOCK_ACQUISITION_FAILED
            );
        }
        return uuid;
    }

    public void unlock(String key, String uuid) {
        lockRedisRepository.unlock(key, uuid);
    }

    public String createSessionAndSeatLockKey(Long sessionId, Long seatId) {
        return "lock:session:" + sessionId + "seat:" + seatId;
    }
}
