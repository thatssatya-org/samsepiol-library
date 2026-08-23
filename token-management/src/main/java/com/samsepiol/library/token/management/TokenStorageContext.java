package com.samsepiol.library.token.management;

import java.util.Objects;

/**
 * Server-supplied encryption context. The key identifier and reference are never accepted from token clients.
 */
public record TokenStorageContext(TokenReference reference, String keyId) {
    public TokenStorageContext {
        reference = Objects.requireNonNull(reference, "reference must not be null");
        keyId = required(keyId, "keyId");
    }

    private static String required(String value, String field) {
        var result = Objects.requireNonNull(value, field + " must not be null");
        if (result.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return result;
    }
}
