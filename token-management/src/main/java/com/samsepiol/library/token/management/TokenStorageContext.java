package com.samsepiol.library.token.management;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/**
 * Server-supplied encryption context. The key identifier and reference are never accepted from token clients.
 */
@Value
@Builder
@ToString
public class TokenStorageContext {
    @NonNull TokenReference reference;
    @NonNull String keyId;

    public static TokenStorageContext required(TokenReference reference, String keyId) {
        if (reference == null || keyId == null || keyId.isBlank()) {
            throw new TokenManagementException();
        }
        return TokenStorageContext.builder().reference(reference).keyId(keyId).build();
    }
}
