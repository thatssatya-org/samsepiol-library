package com.samsepiol.library.cache.local;

import com.samsepiol.library.cache.Cache;
import com.samsepiol.library.cache.models.enums.Type;

public interface LocalCache<K, V> extends Cache<K, V> {

    @Override
    default Type type() {
        return Type.LOCAL;
    }

}
