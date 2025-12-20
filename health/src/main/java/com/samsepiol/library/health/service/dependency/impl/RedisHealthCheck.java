package com.samsepiol.library.health.service.dependency.impl;

import com.samsepiol.library.cache.Cache;
import com.samsepiol.library.health.service.dependency.DependencyHealthCheck;
import com.samsepiol.library.health.service.dependency.models.enums.DependencyType;
import com.samsepiol.library.redis.constants.BeanNames;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty("spring.data.redis.host")
public class RedisHealthCheck implements DependencyHealthCheck {
    @Qualifier(BeanNames.REDIS_CACHE)
    private final Cache<String, String> redisCache;

    @Override
    public @NonNull Supplier<Boolean> healthCheck() {
        return redisCache::isHealthy;
    }

    @Override
    public @NonNull DependencyType getType() {
        return DependencyType.REDIS;
    }
}
