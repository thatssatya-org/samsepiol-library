package com.samsepiol.library.core.security.management;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

/**
 * A management operation request built from an already authenticated upstream identity.
 * Do not place bearer tokens, headers, or other credentials in attributes.
 */
@Value
@Builder
@Jacksonized
@ToString
public class ManagementAuthorizationRequest {
    @NonNull String principalId;
    @NonNull String operation;
    @NonNull @Builder.Default Map<String, String> attributes = Map.of();
}
