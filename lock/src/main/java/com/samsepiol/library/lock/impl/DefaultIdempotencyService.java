package com.samsepiol.library.lock.impl;

import com.samsepiol.library.core.util.IdentityUtils;
import com.samsepiol.library.lock.IdempotencyService;
import com.samsepiol.library.lock.exception.ParallelLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty("spring.data.redis.host")
public class DefaultIdempotencyService implements IdempotencyService {
    private static final String LOCK_PREFIX = "lock:";
    private static final Duration DEFAULT_LOCK_TTL = Duration.ofHours(1);
    private final RedisTemplate<String, Object> redisTemplate;

    // LUA SCRIPT: Atomically check if the key holds the expected value, and if so, delete it.
    // This prevents Thread A from deleting Thread B's lock if A's lock expired.
    private static final String RELEASE_LOCK_SCRIPT =
            """
            if redis.call('get', KEYS[1]) == ARGV[1] then
               return redis.call('del', KEYS[1])
            else
               return 0
            end
            """;

    @Override
    public <T> T execute(String idempotencyKey, Supplier<T> supplier) throws ParallelLockException {
        var lockKey = LOCK_PREFIX + idempotencyKey;
        var lockValue = IdentityUtils.generateId(LOCK_PREFIX);

        if (tryLock(lockKey, lockValue)) {
            try {
                return supplier.get();
            } finally {
                releaseLock(lockKey, lockValue);
            }
        } else {
            throw ParallelLockException.create();
        }
    }

    private boolean tryLock(String lockKey, String lockValue) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, DEFAULT_LOCK_TTL));
    }

    private void releaseLock(String key, String value) {
        var redisScript = new DefaultRedisScript<Long>();
        redisScript.setScriptText(RELEASE_LOCK_SCRIPT);
        redisScript.setResultType(Long.class);

        redisTemplate.execute(redisScript, Collections.singletonList(key), value);
    }
}
