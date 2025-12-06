package com.samsepiol.library.guava;

import com.google.common.cache.CacheBuilder;
import com.samsepiol.library.cache.local.LocalCache;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class GuavaCache<K, V> implements LocalCache<K, V> {
    private final com.google.common.cache.Cache<K, V> cache;

    public GuavaCache(Duration expireDuration) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(expireDuration)
                .build();
    }

    public static <K, V> GuavaCache<K, V> getInstance(Duration expireDuration) {
        return new GuavaCache<>(expireDuration);
    }

    @Override
    public V get(@NonNull K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public @NonNull V put(@NonNull K key, @NonNull V value) {
        cache.put(key, value);
        return value;
    }

    @Override
    public @NonNull V putWithTtl(@NonNull K key, @NonNull V value, @NonNull Duration duration) {
        log.warn("putWithTtl called for guava cache");
        cache.put(key, value);
        return value;
    }

    @Override
    public boolean delete(@NonNull K key) {
        cache.invalidate(key);
        return Boolean.TRUE;
    }

    @Override
    public boolean isHealthy() {
        try {
            cache.size();
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }
}
