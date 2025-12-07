package com.samsepiol.library.redis;

import com.samsepiol.library.cache.distributed.DistributedCache;
import com.samsepiol.library.redis.constants.BeanNames;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Primary
@Repository(BeanNames.REDIS_CACHE)
@RequiredArgsConstructor
@ConditionalOnProperty("spring.data.redis.host")
public class RedisCache<V> implements DistributedCache<String, V> {
    private final RedisTemplate<String, Object> template;
    private static final String PONG = "PONG";

    @Override
    public V get(@NonNull String key) {
        //noinspection unchecked
        return (V) template.opsForValue().get(key);
    }

    @Override
    public List<V> get(@NonNull List<String> keys) {
        //noinspection unchecked
        return (List<V>) template.opsForValue().multiGet(keys);
    }

    @Override
    public @NonNull V put(@NonNull String key, @NonNull V value) {
        template.opsForValue().set(key, value);
        return value;
    }

    @Override
    public @NonNull Map<String, V> put(@NonNull Map<String, V> values) {
        template.opsForValue().multiSet(values);
        return values;
    }

    @Override
    public boolean delete(@NonNull String key) {
        template.delete(key);
        return Boolean.TRUE;
    }

    @Override
    public boolean delete(@NonNull List<String> keys) {
        template.delete(keys);
        return Boolean.TRUE;
    }

    @Override
    public boolean isHealthy() {
        try {
            var connectionFactory = template.getConnectionFactory();
            if (Objects.nonNull(connectionFactory)) {
                var connection = connectionFactory.getConnection();
                return PONG.equals(connection.ping());
            }
        } catch (Exception ignored) {}

        return Boolean.FALSE;
    }

    @Override
    public @NonNull V putWithTtl(@NonNull String key, @NonNull V value, @NonNull Duration duration) {
        template.opsForValue().set(key, value, (int) duration.toSeconds(), TimeUnit.SECONDS);
        return value;
    }
}
