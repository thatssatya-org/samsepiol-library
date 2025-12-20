package com.samsepiol.library.health.service.impl;

import com.samsepiol.library.health.service.HealthCheckService;
import com.samsepiol.library.health.service.dependency.DependencyHealthCheck;
import com.samsepiol.library.health.service.dependency.models.enums.DependencyType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultHealthCheckService implements HealthCheckService {
    private final Map<DependencyType, DependencyHealthCheck> dependencyHealthChecks;

    @PostConstruct
    public void init() {
        isHealthy();
    }

    @Override
    public boolean isHealthy() {
        return dependencyHealthChecks.values().stream().allMatch(DependencyHealthCheck::isHealthy);
    }
}
