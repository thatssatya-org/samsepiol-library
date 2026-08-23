package com.samsepiol.library.core.security.management;

import java.util.Objects;

/**
 * Safe fallback for applications that have not installed an explicit management authorization policy.
 */
public final class DefaultDenyManagementAuthorization implements ManagementAuthorizationBoundary {
    @Override
    public void requireAuthorized(ManagementAuthorizationRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        throw new ManagementAuthorizationDeniedException();
    }
}
