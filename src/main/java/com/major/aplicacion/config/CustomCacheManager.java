package com.major.aplicacion.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.cache.caffeine.CaffeineCache;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CustomCacheManager {
    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        // Expire after access because they not change, expiration is only for resources liberation
        // Low size and low time to expire
        CaffeineCache tokenCache = buildCacheExpireAfterAccess(
                "tokenRolCache", ticker, 75, 1024
        );

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(tokenCache));
        return manager;
    }


    /**
     * Build cache with expire after access policies
     *
     * @param name            cache name
     * @param ticker          ticker
     * @param minutesToExpire minutes to expire
     * @param size            cache size
     * @return created cache
     */
    private CaffeineCache buildCacheExpireAfterAccess(String name, Ticker ticker, int minutesToExpire, int size) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterAccess(minutesToExpire, TimeUnit.MINUTES)
                .maximumSize(size)
                .ticker(ticker)
                .build());
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}