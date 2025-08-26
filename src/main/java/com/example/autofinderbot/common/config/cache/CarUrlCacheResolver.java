package com.example.autofinderbot.common.config.cache;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component("carUrlCacheResolver")
@RequiredArgsConstructor
class CarUrlCacheResolver implements CacheResolver {
    private final CacheManager cacheManager;
    private final CacheProperties cacheProps;

    @NotNull
    @Override
    public Collection<? extends Cache> resolveCaches(@NotNull CacheOperationInvocationContext<?> context) {
        Cache cache = cacheManager.getCache(cacheProps.getCarUrl());
        return List.of(cache);
    }
}

