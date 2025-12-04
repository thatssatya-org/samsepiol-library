package com.samsepiol.message.queue.core.config;

import com.samsepiol.message.queue.core.MessageHandler;
import com.samsepiol.message.queue.core.models.MessageHandlerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MessageHandlerBeanConfig {

    @Bean
    @Primary
    public Map<MessageHandlerType, MessageHandler> messageHandlers(List<MessageHandler> messageHandlers) {
        return messageHandlers.stream()
                .collect(Collectors.toUnmodifiableMap(MessageHandler::getType, Function.identity()));
    }
}
