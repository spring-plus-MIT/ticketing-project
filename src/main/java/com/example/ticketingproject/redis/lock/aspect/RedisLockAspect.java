package com.example.ticketingproject.redis.lock.aspect;

import com.example.ticketingproject.redis.lock.annotation.RedisLock;
import com.example.ticketingproject.redis.lock.enums.LockStrategy;
import com.example.ticketingproject.redis.lock.service.LockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;


@Aspect // Aspect = Spring AOP 대상
@Component
@RequiredArgsConstructor
public class RedisLockAspect {
    private final LockService lockService;

    private final ExpressionParser parser = new SpelExpressionParser();

    // @RedisLock 어노테이션이 붙은 메서드 실행전에 applyLock 실행
    // ProceedingJoinPoint = 원래 실행하려던 메서드의 정보(메서드 이름, 파라미터, 클래스 등)를 담고 있음
    // .proceed()를 호출해야 실제 메서드가 실행 됨
    // 즉 현재 프로젝트 에선 ProceedingJoinPoint에 @RedisLock 어노테이션이 붙은
    // SeatService 클래스에 save 메서드에 정보를 담고 있으며 applyLock을 먼저 실행 후
    // joinPoint.proceed()를 통해 원래 실행하려던 (좌석 생성) 메서드를 실행
    @Around("@annotation(redisLock)")
    public Object applyLock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        String key = generateKey(joinPoint, redisLock.key());

        String uuid;

        if (redisLock.strategy() == LockStrategy.RETRY) {
            uuid = lockService.lockRetry(key);
        } else {
            uuid = lockService.lockFailFast(key);
        }

        try {
            return joinPoint.proceed();
        } finally {
            lockService.unlock(key, uuid);
        }
    }

    public String generateKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        // joinPoint에 저장된 현재 실행되는 메서드 정보
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 해당 메서드에 파라미터 이름
        String[] parameterNames = signature.getParameterNames();
        // 해당 파라미터 값
        Object[] args = joinPoint.getArgs();

        // SpEL 변수 저장 예) context.setVariable(parameterNames[i], args[i]) 는 venueId = 3
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // 추출한 파라미터 값 + key 양식 합치기
        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
