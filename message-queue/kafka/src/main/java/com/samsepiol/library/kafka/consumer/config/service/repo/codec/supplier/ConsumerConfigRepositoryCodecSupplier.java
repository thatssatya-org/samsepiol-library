package com.samsepiol.library.kafka.consumer.config.service.repo.codec.supplier;

import com.samsepiol.library.kafka.consumer.config.service.repo.models.ConsumerConfigEntity;
import com.samsepiol.library.mongo.codec.CodecSupplier;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerConfigRepositoryCodecSupplier implements CodecSupplier {
    private static final List<Class<?>> CLASSES = List.of(ConsumerConfigEntity.class);

    @Override
    public @NonNull List<Class<?>> getManagedClasses() {
        return CLASSES;
    }
}
