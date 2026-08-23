package com.samsepiol.library.core.security.management;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A management operation request built from an already authenticated upstream identity.
 * Do not place bearer tokens, headers, or other credentials in attributes.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class ManagementAuthorizationRequest {
    private final String principalId;
    private final String operation;
    private final Map<String, String> attributes;

    public ManagementAuthorizationRequest(String principalId, String operation, Map<String, String> attributes) {
        this.principalId = requireText(principalId, "principalId");
        this.operation = requireText(operation, "operation");
        this.attributes = immutableCopy(attributes);
    }

    private static String requireText(String value, String field) {
        var result = Objects.requireNonNull(value, field + " must not be null");
        if (result.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return result;
    }

    private static Map<String, String> immutableCopy(Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    }
}
