package com.example.autofinderbot.common.config.cache;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    public CacheManager cacheManager(Cache<Object, Object> caffeine, CacheProperties props) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache(props.getCarUrl(), caffeine);
        return cacheManager;
    }

    @Bean
    public Cache<Object, Object> caffeine(CacheProperties props) {
        return Caffeine.newBuilder()
                .maximumSize(props.getMaxSize())
                .recordStats()
                .build();
    }
}
