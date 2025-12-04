package com.samsepiol.library.kafka.consumer.config.service.repo;

import com.samsepiol.library.kafka.consumer.config.service.repo.models.ConsumerConfigEntity;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface ConsumerConfigRepository {
    @NonNull
    List<ConsumerConfigEntity> findActive();

    @NonNull List<ConsumerConfigEntity> findInactive();

    @NonNull
    Optional<ConsumerConfigEntity> findById(@NonNull String id);

    @NonNull
    ConsumerConfigEntity insert(@NonNull ConsumerConfigEntity consumerConfigEntity);

    boolean markInactive(@NonNull String id);

    boolean markActive(@NonNull String id);
}
