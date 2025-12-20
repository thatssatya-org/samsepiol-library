package com.samsepiol.library.health.service.dependency.impl;

import com.samsepiol.library.health.service.dependency.DependencyHealthCheck;
import com.samsepiol.library.health.service.dependency.models.enums.DependencyType;
import com.samsepiol.library.mongo.Repository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@ConditionalOnBean(Repository.class)
@RequiredArgsConstructor
public class MongoDBHealthCheck implements DependencyHealthCheck {
    private final Repository repository;

    @Override
    public @NonNull Supplier<Boolean> healthCheck() {
        return repository::isHealthy;
    }

    @Override
    public @NonNull DependencyType getType() {
        return DependencyType.MONGO_DB;
    }
}
