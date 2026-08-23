package com.samsepiol.library.token.management.api;

import lombok.NonNull;

/**
 * Application adapter that resolves the authenticated operator and server-owned token scope for the current request.
 */
@FunctionalInterface
public interface TokenManagementRequestContextResolver {
    @NonNull
    TokenManagementRequestContext resolve();
}
