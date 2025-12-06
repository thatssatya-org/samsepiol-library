package com.samsepiol.library.kafka.consumer.client;

import com.samsepiol.library.kafka.consumer.config.service.repo.models.ConsumerConfigEntity;
import lombok.NonNull;

import java.util.List;

public interface MessageConsumerClient {
    List<ConsumerConfigEntity> init(List<ConsumerConfigEntity> entities);

    @NonNull
    ConsumerConfigEntity init(@NonNull ConsumerConfigEntity config);

    void stopConsumer(@NonNull String id);
}
