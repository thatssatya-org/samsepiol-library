package com.samsepiol.library.token.management.api;

import com.samsepiol.library.core.security.management.ManagementAuthorizationRequest;
import com.samsepiol.library.token.management.TokenStorageContext;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/**
 * Server-derived context for a token write. This is deliberately not deserialized from client input.
 */
@Value
@Builder
@ToString
public class TokenManagementRequestContext {
    @NonNull TokenStorageContext storageContext;
    @NonNull ManagementAuthorizationRequest authorizationRequest;
}
