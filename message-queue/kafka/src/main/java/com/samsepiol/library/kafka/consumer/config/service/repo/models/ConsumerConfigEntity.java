package com.samsepiol.library.kafka.consumer.config.service.repo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.samsepiol.library.kafka.consumer.config.service.repo.models.enums.ConsumerStatus;
import com.samsepiol.library.mongo.models.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Value
@SuperBuilder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(onConstructor_ = {@BsonCreator})
public class ConsumerConfigEntity extends Entity {
    private static final String ID_PREFIX = "CE";
    public static final String STATUS_KEY = "status";

    @NonNull
    @BsonProperty("topic")
    String topic;

    @NonNull
    @BsonProperty("messageHandler")
    String messageHandler;

    @NonNull
    @Builder.Default
    @BsonProperty("concurrency")
    Integer concurrency = 1;

    @NonNull
    @Builder.Default
    @BsonProperty(STATUS_KEY)
    ConsumerStatus status = ConsumerStatus.INACTIVE;

    @Override
    protected @NonNull String getIdPrefix() {
        return ID_PREFIX;
    }
}
