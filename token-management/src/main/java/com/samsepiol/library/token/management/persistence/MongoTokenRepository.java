package com.samsepiol.library.token.management.persistence;

import com.mongodb.client.model.Filters;
import com.samsepiol.library.mongo.Repository;
import com.samsepiol.library.token.management.TokenReference;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
@ConditionalOnExpression("!'${spring.data.mongodb.uri:}'.isBlank() || !'${spring.data.mongodb.host:}'.isBlank()")
public class MongoTokenRepository implements TokenRepository {
    private static final String COLLECTION_NAME = "integration-tokens";
    private final Repository repository;

    @PostConstruct
    void ensureIndexes() {
        repository.ensureUniqueIndex(COLLECTION_NAME, "integration-token-reference-unique", "namespace", "subject", "name");
    }

    @Override
    public @Nullable TokenRecordEntity find(@NonNull TokenReference reference) {
        return repository.findById(COLLECTION_NAME, reference.persistentId(), TokenRecordEntity.class);
    }

    @Override
    public @NonNull TokenRecordEntity upsert(@NonNull TokenRecordEntity record) {
        return repository.upsert(COLLECTION_NAME, record, Filters.and(
                Filters.eq("namespace", record.getNamespace()), Filters.eq("subject", record.getSubject()),
                Filters.eq("name", record.getName())));
    }
}
