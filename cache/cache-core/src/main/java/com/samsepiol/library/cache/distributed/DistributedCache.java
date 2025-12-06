package com.samsepiol.library.cache.distributed;

import com.samsepiol.library.cache.Cache;
import com.samsepiol.library.cache.models.enums.Type;

public interface DistributedCache<K, V> extends Cache<K, V> {

    @Override
    default Type type() {
        return Type.DISTRIBUTED;
    }

}
