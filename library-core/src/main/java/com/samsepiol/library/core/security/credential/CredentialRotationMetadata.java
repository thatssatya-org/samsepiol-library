package com.samsepiol.library.core.security.credential;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * Non-secret audit metadata for a credential envelope key rotation.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class CredentialRotationMetadata {
    private final String previousKeyId;
    private final long rotatedAtEpochMillis;

    @JsonCreator
    public CredentialRotationMetadata(
            @JsonProperty("previousKeyId") String previousKeyId,
            @JsonProperty("rotatedAtEpochMillis") long rotatedAtEpochMillis) {
        this.previousKeyId = requireText(previousKeyId, "previousKeyId");
        if (rotatedAtEpochMillis <= 0) {
            throw new IllegalArgumentException("rotatedAtEpochMillis must be positive");
        }
        this.rotatedAtEpochMillis = rotatedAtEpochMillis;
    }

    private static String requireText(String value, String field) {
        var result = Objects.requireNonNull(value, field + " must not be null");
        if (result.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return result;
    }
}
