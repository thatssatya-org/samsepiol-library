package com.samsepiol.message.queue.core.models;

import lombok.NonNull;

public interface MessageHandlerType {

    @NonNull
    String name();
}
