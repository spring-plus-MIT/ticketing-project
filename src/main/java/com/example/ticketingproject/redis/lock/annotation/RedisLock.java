package com.example.ticketingproject.redis.lock.annotation;

import com.example.ticketingproject.redis.lock.enums.LockStrategy;

import java.lang.annotation.*;

// Target = 어노테이션을 붙일 수 있는 위치
// 예) TYPE = 클래스, FIELD = 필드, METHOD = 메서드, PARAMETER = 파라미터
// Retention = 어노테이션이 언제까지 유지되는지
// 예) SOURCE = 컴파일 후 제거, CLASS = class 파일까지만, RUNTIME = 실행 시에도 유지
// Inherited = 부모 클래스에 붙은 어노테이션을 자식 클래스가 자동으로 상속 받도록 하는 어노테이션
// 클래스에만 적용되는 어노테이션으로 현재 프로젝트 구조상 Target = 메서드 이기에 필요 없음
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock { // 기존 인터페이스가 아닌 어노테이션 인터페이스로 만듬(메서드 구현 X, 값 설정)

    // 어노테이션 속성 설정
    // 예) @RedisLock(key = "lock:abcde", strategy = LockStrategy.RETRY)
    // AOP에서 key, strategy 확인 -> lockService.lock메서드() 호출
    // Aspect에서 @Around("@annotation(redisLock)") -> redisLock.key(), redisLock.strategy() 접근 가능
    String key();

    LockStrategy strategy() default LockStrategy.FAIL_FAST;
}
