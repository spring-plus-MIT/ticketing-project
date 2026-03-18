package com.example.ticketingproject.redis;

import com.example.ticketingproject.common.config.SuperAdminInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RedisTest {

    @MockBean
    private SuperAdminInitializer superAdminInitializer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void Redis_연결_테스트() {
        redisTemplate.opsForValue().set("테스트", "성공");

        Object value = redisTemplate.opsForValue().get("테스트");

        System.out.println(value);
    }
}
