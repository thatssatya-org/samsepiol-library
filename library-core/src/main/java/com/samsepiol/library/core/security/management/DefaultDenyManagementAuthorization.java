package com.samsepiol.library.core.security.management;

import lombok.NonNull;

/**
 * Safe fallback for applications that have not installed an explicit management authorization policy.
 */
public final class DefaultDenyManagementAuthorization implements ManagementAuthorizationBoundary {
    @Override
    public void requireAuthorized(@NonNull ManagementAuthorizationRequest request) {
        throw new ManagementAuthorizationDeniedException();
    }
}
