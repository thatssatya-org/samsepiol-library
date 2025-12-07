package com.samsepiol.library.cache;

import com.samsepiol.library.cache.models.enums.Type;
import lombok.NonNull;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface Cache<K, V> {

    V get(@NonNull K key);

    List<V> get(@NonNull List<K> keys);

    @NonNull
    V put(@NonNull K key, @NonNull V value);

    @NonNull
    Map<K, V> put(@NonNull Map<K, V> values);

    @NonNull
    V putWithTtl(@NonNull K key, @NonNull V value, @NonNull Duration duration);

    boolean delete(@NonNull K key);

    boolean delete(@NonNull List<String> keys);

    Type type();

    boolean isHealthy();

}
