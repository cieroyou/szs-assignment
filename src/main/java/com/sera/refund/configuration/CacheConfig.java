package com.sera.refund.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {
//    @Bean
//    public CacheManager cacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager("scrapingCache"); // ✅ 캐시 이름 등록
//        cacheManager.setCaffeine(caffeineCache());
//        return cacheManager;
//    }

    @Bean
    public Caffeine<Object, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS) // 24시간 후 캐시 자동 삭제
                .maximumSize(1000); //  최대 1000개 데이터 캐싱
    }
}
