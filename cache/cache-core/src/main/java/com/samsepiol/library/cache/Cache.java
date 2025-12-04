package com.samsepiol.library.cache;

import com.samsepiol.library.cache.models.enums.Type;
import lombok.NonNull;

import java.time.Duration;

public interface Cache<K, V> {

    V get(@NonNull K key);

    @NonNull
    V put(@NonNull K key, @NonNull V value);

    @NonNull
    V putWithTtl(@NonNull K key, @NonNull V value, @NonNull Duration duration);

    boolean delete(@NonNull K key);

    Type type();

    boolean isHealthy();

}
