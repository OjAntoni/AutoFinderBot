package com.example.autofinderbot.config;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String CAR_URLS_CACHE = "car_urls";
    public static final String CAR_SEARCH_CACHE = "car_search";

    @Bean
    public CacheManager cacheManager(Cache<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache(CAR_URLS_CACHE, caffeine);
        cacheManager.registerCustomCache(CAR_SEARCH_CACHE, caffeine);
        return cacheManager;
    }

    @Bean
    Cache<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .recordStats()
                .build();
    }
}
