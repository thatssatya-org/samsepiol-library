package com.samsepiol.library.kafka.consumer.message.listener;

import com.samsepiol.message.queue.core.MessageHandler;
import com.samsepiol.message.queue.core.models.request.MessageHandlerRequest;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicMessageListener implements AcknowledgingMessageListener<String, String> {
    private final MessageHandler messageHandler;

    public static @NonNull DynamicMessageListener from(@NonNull MessageHandler messageHandler) {
        return new DynamicMessageListener(messageHandler);
    }

    @Override
    public void onMessage(@NonNull ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        handleMessage(data);

        if (Objects.nonNull(acknowledgment)) {
            acknowledgment.acknowledge();
        }
    }

    private void handleMessage(ConsumerRecord<String, String> data) {
        var headers = Arrays.stream(data.headers().toArray())
                .filter(header -> Objects.nonNull(header.value()))
                .collect(Collectors.toMap(
                        Header::key,
                        header -> new String(header.value(), StandardCharsets.UTF_8),
                        (existing, replacement) -> replacement
                ));

        var request = MessageHandlerRequest.builder()
                .key(data.key())
                .value(data.value())
                .headers(headers)
                .build();

        messageHandler.process(request);
    }
}
