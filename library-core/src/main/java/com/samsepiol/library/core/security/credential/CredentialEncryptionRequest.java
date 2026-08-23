package com.samsepiol.library.core.security.credential;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * Ephemeral encryption input. Do not persist or log instances of this type.
 */
@Getter
@EqualsAndHashCode
public final class CredentialEncryptionRequest {
    private final byte[] plaintext;
    private final String keyId;
    private final byte[] authenticatedData;

    public CredentialEncryptionRequest(byte[] plaintext, String keyId, byte[] authenticatedData) {
        this.plaintext = copyRequired(plaintext, "plaintext");
        this.keyId = requireText(keyId, "keyId");
        this.authenticatedData = authenticatedData == null ? new byte[0] : Arrays.copyOf(authenticatedData, authenticatedData.length);
    }

    public byte[] getPlaintext() {
        return Arrays.copyOf(plaintext, plaintext.length);
    }

    public byte[] getAuthenticatedData() {
        return Arrays.copyOf(authenticatedData, authenticatedData.length);
    }

    @Override
    public String toString() {
        return "CredentialEncryptionRequest{keyId='" + keyId + "', plaintextLength=" + plaintext.length
                + ", authenticatedDataLength=" + authenticatedData.length + '}';
    }

    private static String requireText(String value, String field) {
        var result = Objects.requireNonNull(value, field + " must not be null");
        if (result.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return result;
    }

    private static byte[] copyRequired(byte[] value, String field) {
        var result = Objects.requireNonNull(value, field + " must not be null");
        if (result.length == 0) {
            throw new IllegalArgumentException(field + " must not be empty");
        }
        return Arrays.copyOf(result, result.length);
    }
}
