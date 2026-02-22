package com.samsepiol.library.kafka.consumer.client.impl;

import com.samsepiol.library.kafka.consumer.client.MessageConsumerClient;
import com.samsepiol.library.kafka.consumer.config.service.repo.models.ConsumerConfigEntity;
import com.samsepiol.library.kafka.consumer.config.service.repo.models.enums.ConsumerStatus;
import com.samsepiol.library.kafka.consumer.message.listener.DynamicMessageListener;
import com.samsepiol.message.queue.core.MessageHandler;
import com.samsepiol.message.queue.core.models.MessageHandlerType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.backoff.FixedBackOff;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnExpression("!'${spring.kafka.bootstrap-servers:}'.isBlank()")
public class DefaultMessageConsumerClient implements MessageConsumerClient, DisposableBean {
    private final ConsumerFactory<String, String> consumerFactory;
    private final Map<MessageHandlerType, MessageHandler> messageHandlers;
    private Map<String, MessageHandler> messageHandlerMap;
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> containers = new ConcurrentHashMap<>();

    @Override
    public List<ConsumerConfigEntity> init(List<ConsumerConfigEntity> entities) {
        if (Objects.isNull(messageHandlerMap)) {
            messageHandlerMap = messageHandlers.entrySet().stream()
                    .collect(Collectors.toUnmodifiableMap(
                            entry -> entry.getKey().name(), Map.Entry::getValue));
        }
        log.info("Initializing consumers");
        entities.forEach(config -> {
            try {
                init(config); // Start without saving to DB again
            } catch (Exception e) {
                log.error("Failed to restore consumer: {}", config.getId(), e);
            }
        });
        return entities;
    }

    @Override
    public void stopConsumer(@NonNull String id) {
        var container = containers.get(id);
        if (container != null) {
            container.stop();
            containers.remove(id);
        }
        log.info("Stopped consumer: {}", id);
    }

    @Override
    public @NonNull ConsumerConfigEntity init(@NonNull ConsumerConfigEntity config) {
        if (config.getStatus() == ConsumerStatus.ACTIVE && !containers.containsKey(config.getId())) {
            var messageHandler = messageHandlerMap.get(config.getMessageHandler());
            if (Objects.isNull(messageHandler)) {
                throw new IllegalArgumentException("Unknown strategy: " + config.getMessageHandler());
            }

            var props = new ContainerProperties(config.getTopic());
            props.setGroupId(messageHandler.getType().name());
            props.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
            props.setMessageListener(DynamicMessageListener.from(messageHandler));

            var container = new ConcurrentMessageListenerContainer<>(consumerFactory, props);
            container.setConcurrency(config.getConcurrency());

            container.setCommonErrorHandler(createInfiniteRetryHandler());
            container.start();
            containers.put(config.getId(), container);
            log.info("Started consumer: {}, {}", config.getId(), messageHandler.getType().name());
        }
        return config;
    }

    private DefaultErrorHandler createInfiniteRetryHandler() {
        var backOff = new FixedBackOff(5000L, FixedBackOff.UNLIMITED_ATTEMPTS);
        return new DefaultErrorHandler(null, backOff);
    }

    @Override
    public void destroy() {
        containers.values().forEach(container -> {
            if (container.isRunning()) {
                container.stop();
            }
        });
    }
}
