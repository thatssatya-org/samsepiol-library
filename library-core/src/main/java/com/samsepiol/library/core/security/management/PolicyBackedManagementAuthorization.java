package com.samsepiol.library.core.security.management;

import java.util.function.Predicate;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Adapter for an application-owned policy. The library deliberately does not authenticate network credentials.
 */
@Builder
@RequiredArgsConstructor
public final class PolicyBackedManagementAuthorization implements ManagementAuthorizationBoundary {
    private final @NonNull Predicate<ManagementAuthorizationRequest> policy;

    @Override
    public void requireAuthorized(@NonNull ManagementAuthorizationRequest request) {
        if (!policy.test(request)) {
            throw new ManagementAuthorizationDeniedException();
        }
    }
}
