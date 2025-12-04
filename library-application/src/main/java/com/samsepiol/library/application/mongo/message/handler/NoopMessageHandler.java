package com.samsepiol.library.application.mongo.message.handler;

import com.samsepiol.message.queue.core.MessageHandler;
import com.samsepiol.message.queue.core.models.MessageHandlerType;
import com.samsepiol.message.queue.core.models.request.MessageHandlerRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoopMessageHandler implements MessageHandler {
    @Override
    public void process(MessageHandlerRequest request) {
        log.info("Message processed: {} {}", request.getKey(), request.getValue());
    }

    @Override
    public @NonNull MessageHandlerType getType() {
        return LibraryMessageHandler.NOOP;
    }
}
