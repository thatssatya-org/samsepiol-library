package com.samsepiol.message.queue.core;

import com.samsepiol.message.queue.core.models.MessageHandlerType;
import com.samsepiol.message.queue.core.models.request.MessageHandlerRequest;
import lombok.NonNull;

public interface MessageHandler {

    void process(MessageHandlerRequest request);

    @NonNull
    MessageHandlerType getType();

}