package com.example.ticketingproject.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(List.of(
                new CaffeineCache("performanceSearch",
                        Caffeine.newBuilder()
                                .maximumSize(100)
                                .expireAfterWrite(5, TimeUnit.MINUTES)
                                .build()),
                new CaffeineCache("popularKeywords",
                        Caffeine.newBuilder()
                                .maximumSize(10)
                                .expireAfterWrite(1, TimeUnit.MINUTES)
                                .build())
        ));
        return simpleCacheManager;
    }
}
