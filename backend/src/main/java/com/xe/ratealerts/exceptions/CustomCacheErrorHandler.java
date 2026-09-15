package com.xe.ratealerts.exceptions;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Log4j2
public class CustomCacheErrorHandler implements CacheErrorHandler {
    private static final String CACHE_ERROR = "An error occurred while processing the cache request";

    @Override
    public void handleCacheGetError(@NotNull RuntimeException exception, Cache cache, @NotNull Object key) {
        log.error("Error getting object from {} cache, exception: {}", cache.getName(), exception);
        throw new CustomException(CACHE_ERROR);
    }

    @Override
    public void handleCachePutError(@NotNull RuntimeException exception, Cache cache, @NotNull Object key, Object value) {
        log.error("Error putting object into {} cache, exception: {}", cache.getName(), exception);
        throw new CustomException(CACHE_ERROR);
    }

    @Override
    public void handleCacheEvictError(@NotNull RuntimeException exception, Cache cache, @NotNull Object key) {
        log.error("Error evicting {} from the {} cache, exception: {}", key, cache.getName(), exception);
        throw new CustomException(CACHE_ERROR);
    }

    @Override
    public void handleCacheClearError(@NotNull RuntimeException exception, @NotNull Cache cache) {
        log.error("Error clearing {} cache, exception: {}", cache.getName(), exception);
        throw new CustomException(CACHE_ERROR);
    }
}

