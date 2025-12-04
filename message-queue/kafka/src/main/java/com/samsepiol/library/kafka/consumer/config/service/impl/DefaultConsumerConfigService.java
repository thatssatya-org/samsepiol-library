package com.samsepiol.library.kafka.consumer.config.service.impl;

import com.samsepiol.library.kafka.consumer.client.MessageConsumerClient;
import com.samsepiol.library.kafka.consumer.config.service.ConsumerConfigService;
import com.samsepiol.library.kafka.consumer.config.service.repo.ConsumerConfigRepository;
import com.samsepiol.library.kafka.consumer.config.service.repo.models.ConsumerConfigEntity;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty("spring.kafka.bootstrap-servers")
public class DefaultConsumerConfigService implements ConsumerConfigService {
    private final ConsumerConfigRepository repository;
    private final MessageConsumerClient consumerClient;

    @PostConstruct
    public void init() {
        consumerClient.init(findActive());
    }

    @Override
    public @NonNull List<ConsumerConfigEntity> findActive() {
        return repository.findActive();
    }

    @Override
    public @NonNull Optional<ConsumerConfigEntity> findById(@NonNull String id) {
        return repository.findById(id);
    }

    @Override
    public @NonNull ConsumerConfigEntity insert(@NonNull ConsumerConfigEntity consumerConfigEntity) {
        var entity = repository.insert(consumerConfigEntity);
        return consumerClient.init(entity);
    }

    @Override
    public boolean markActive(@NonNull String id) {
        var result = repository.markActive(id);
        findById(id).ifPresent(consumerClient::init);
        return result;
    }

    @Override
    public boolean markInactive(@NonNull String id) {
        var result = repository.markInactive(id);
        consumerClient.stopConsumer(id);
        return result;
    }
}
