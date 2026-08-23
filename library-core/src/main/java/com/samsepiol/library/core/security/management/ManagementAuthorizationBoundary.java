package com.samsepiol.library.core.security.management;

/**
 * Default-deny boundary for management actions. Applications should populate requests only after an upstream
 * identity mechanism, such as a private network gateway, has authenticated the caller.
 */
@FunctionalInterface
public interface ManagementAuthorizationBoundary {
    void requireAuthorized(ManagementAuthorizationRequest request);
}
