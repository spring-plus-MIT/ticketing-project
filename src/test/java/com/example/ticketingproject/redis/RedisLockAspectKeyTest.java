package com.example.ticketingproject.redis;

import com.example.ticketingproject.redis.lock.aspect.RedisLockAspect;
import com.example.ticketingproject.redis.lock.service.LockService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisLockAspectKeyTest {

    @InjectMocks
    private RedisLockAspect redisLockAspect;

    @Mock
    private LockService lockService;

    @Test
    void generateKey_생성_성공_테스트() {

        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(signature);

        when(signature.getParameterNames()).thenReturn(new String[] {"venueId"});
        when(joinPoint.getArgs()).thenReturn(new Object[] {1L});

        String keyExpression = "'lock:venue:'+ #venueId + ':seat:create'";

        // when
        String result = redisLockAspect.generateKey(joinPoint, keyExpression);

        // then
        assertEquals("lock:venue:1:seat:create", result);
    }
}
