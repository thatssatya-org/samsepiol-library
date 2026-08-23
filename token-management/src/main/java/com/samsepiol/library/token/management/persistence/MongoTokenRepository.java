package com.samsepiol.library.token.management.persistence;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.samsepiol.library.mongo.Repository;
import com.samsepiol.library.token.management.TokenReference;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
@ConditionalOnExpression("!'${spring.data.mongodb.host:}'.isBlank()")
public class MongoTokenRepository implements TokenRepository {
    private static final String COLLECTION_NAME = "integration-tokens";
    private final Repository repository;

    @PostConstruct
    void ensureIndexes() {
        repository.getCollection(COLLECTION_NAME).createIndex(
                Indexes.ascending("namespace", "subject", "name"),
                new IndexOptions().unique(true).name("integration-token-reference-unique"));
    }

    @Override
    public @NonNull Optional<TokenRecord> find(@NonNull TokenReference reference) {
        return Optional.ofNullable(repository.findById(COLLECTION_NAME, reference.persistentId(), TokenRecord.class));
    }

    @Override
    public @NonNull TokenRecord upsert(@NonNull TokenRecord record) {
        return repository.upsert(COLLECTION_NAME, record);
    }
}
