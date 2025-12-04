package com.samsepiol.library.kafka.consumer.config.service.repo.models;

import com.samsepiol.library.kafka.consumer.config.service.repo.models.enums.ConsumerStatus;
import com.samsepiol.library.mongo.models.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerConfigEntity extends Entity {
    private static final String ID_PREFIX = "CE";
    public static final String STATUS_KEY = "status";

    @NonNull
    private String topic;

    @NonNull
    private String messageHandler;

    @NonNull
    @Builder.Default
    private Integer concurrency = 1;

    @NonNull
    @Builder.Default
    @BsonProperty(STATUS_KEY)
    private ConsumerStatus status = ConsumerStatus.INACTIVE;

    @Override
    protected @NonNull String getIdPrefix() {
        return ID_PREFIX;
    }
}
