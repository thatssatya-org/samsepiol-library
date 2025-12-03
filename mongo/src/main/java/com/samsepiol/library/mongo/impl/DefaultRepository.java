package com.samsepiol.library.mongo.impl;

import com.samsepiol.library.mongo.Repository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@org.springframework.stereotype.Repository
@ConditionalOnProperty("spring.data.mongodb.host")
@Primary
@RequiredArgsConstructor
public class DefaultRepository implements Repository {
    private final MongoTemplate mongoTemplate;
    private final CodecRegistry codecRegistry;

    @Override
    public @NonNull MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    public @NonNull CodecRegistry getCodecRegistry() {
        return codecRegistry;
    }

    @Override
    public boolean isHealthy() {
        try {
            mongoTemplate.getCollectionNames();
            return Boolean.TRUE;
        } catch (Exception exception) {
            log.error("MongoDB unhealthy");
            return Boolean.FALSE;
        }
    }
}
