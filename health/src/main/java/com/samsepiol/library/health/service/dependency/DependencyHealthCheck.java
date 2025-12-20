package com.samsepiol.library.health.service.dependency;

import com.samsepiol.library.health.service.dependency.models.enums.DependencyType;
import com.samsepiol.library.health.service.dependency.models.enums.FailureLevel;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public interface DependencyHealthCheck {
    Logger LOGGER = LoggerFactory.getLogger(DependencyHealthCheck.class);

    default boolean isHealthy() {
        return healthBasedOnFailureLevel(performHealthCheck());
    }

    @NonNull
    Supplier<Boolean> healthCheck();

    @NonNull
    DependencyType getType();

    @NonNull
    default FailureLevel failureLevel() {
        return FailureLevel.APPLICATION_FAILURE;
    }

    private boolean performHealthCheck() {
        try {
            return healthCheck().get();
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    private boolean healthBasedOnFailureLevel(boolean healthy) {
        if (!healthy && failureLevel() != FailureLevel.APPLICATION_FAILURE) {
            LOGGER.warn("[Health Check] - Non application failure dependency - {} unhealthy", getType());
            return Boolean.TRUE;
        }

        if (!healthy) {
            LOGGER.error("[Health Check] - {} unhealthy", getType());
        }

        return healthy;
    }
}
