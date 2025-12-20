package com.samsepiol.library.health.service.dependency.impl;

import com.samsepiol.library.health.service.dependency.DependencyHealthCheck;
import com.samsepiol.library.health.service.dependency.models.enums.DependencyType;
import com.samsepiol.library.temporal.TemporalService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@ConditionalOnProperty("temporal-config.host")
@RequiredArgsConstructor
public class TemporalServiceHealthCheck implements DependencyHealthCheck {
    private final TemporalService service;

    @Override
    public @NonNull Supplier<Boolean> healthCheck() {
        return service::isHealthy;
    }

    @Override
    public @NonNull DependencyType getType() {
        return DependencyType.TEMPORAL;
    }
}
