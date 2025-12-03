package com.samsepiol.library.mongo.codec;

import lombok.NonNull;

import java.util.Collections;
import java.util.List;

public interface CodecSupplier {

    @NonNull
    default List<Class<?>> getManagedClasses() {
        return Collections.emptyList();
    }
}
