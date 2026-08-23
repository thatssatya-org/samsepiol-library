package com.samsepiol.library.core.security.management;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Adapter for an application-owned policy. The library deliberately does not authenticate network credentials.
 */
public final class PolicyBackedManagementAuthorization implements ManagementAuthorizationBoundary {
    private final Predicate<ManagementAuthorizationRequest> policy;

    public PolicyBackedManagementAuthorization(Predicate<ManagementAuthorizationRequest> policy) {
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
    }

    @Override
    public void requireAuthorized(ManagementAuthorizationRequest request) {
        var validatedRequest = Objects.requireNonNull(request, "request must not be null");
        if (!policy.test(validatedRequest)) {
            throw new ManagementAuthorizationDeniedException();
        }
    }
}
