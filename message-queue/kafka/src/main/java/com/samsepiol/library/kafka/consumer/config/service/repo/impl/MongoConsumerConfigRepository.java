package com.samsepiol.library.kafka.consumer.config.service.repo.impl;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.samsepiol.library.kafka.consumer.config.service.repo.ConsumerConfigRepository;
import com.samsepiol.library.kafka.consumer.config.service.repo.models.ConsumerConfigEntity;
import com.samsepiol.library.kafka.consumer.config.service.repo.models.enums.ConsumerStatus;
import com.samsepiol.library.mongo.Repository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
@ConditionalOnExpression("!'${spring.kafka.bootstrap-servers:}'.isBlank()")
public class MongoConsumerConfigRepository implements ConsumerConfigRepository {
    private static final String COLLECTION_NAME = "kafka-message-consumers";
    private final Repository repository;

    @Override
    public @NonNull List<ConsumerConfigEntity> findActive() {
        var query = Filters.eq(ConsumerConfigEntity.STATUS_KEY, ConsumerStatus.ACTIVE);
        return repository.findAll(COLLECTION_NAME, query, ConsumerConfigEntity.class);
    }

    @Override
    public @NonNull List<ConsumerConfigEntity> findInactive() {
        var query = Filters.eq(ConsumerConfigEntity.STATUS_KEY, ConsumerStatus.INACTIVE);
        return repository.findAll(COLLECTION_NAME, query, ConsumerConfigEntity.class);
    }

    @Override
    public @NonNull Optional<ConsumerConfigEntity> findById(@NonNull String id) {
        return Optional.ofNullable(repository.findById(COLLECTION_NAME, id, ConsumerConfigEntity.class));
    }

    @Override
    public @NonNull ConsumerConfigEntity insert(@NonNull ConsumerConfigEntity consumerConfigEntity) {
        return repository.insert(COLLECTION_NAME, consumerConfigEntity, ConsumerConfigEntity.class);
    }

    @Override
    public boolean markInactive(@NonNull String id) {
        var query = Updates.set(ConsumerConfigEntity.STATUS_KEY, ConsumerStatus.INACTIVE);
        return repository.updateById(COLLECTION_NAME, id, query);
    }

    @Override
    public boolean markActive(@NonNull String id) {
        var query = Updates.set(ConsumerConfigEntity.STATUS_KEY, ConsumerStatus.ACTIVE);
        return repository.updateById(COLLECTION_NAME, id, query);
    }
}
