package com.samsepiol.library.core.security.credential;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * Ephemeral decryption input. Authenticated data must match the data used at encryption time.
 */
@Getter
@EqualsAndHashCode
public final class CredentialDecryptionRequest {
    private final CredentialEnvelope envelope;
    private final byte[] authenticatedData;

    public CredentialDecryptionRequest(CredentialEnvelope envelope, byte[] authenticatedData) {
        this.envelope = Objects.requireNonNull(envelope, "envelope must not be null");
        this.authenticatedData = authenticatedData == null ? new byte[0] : Arrays.copyOf(authenticatedData, authenticatedData.length);
    }

    public byte[] getAuthenticatedData() {
        return Arrays.copyOf(authenticatedData, authenticatedData.length);
    }

    @Override
    public String toString() {
        return "CredentialDecryptionRequest{envelope=" + envelope
                + ", authenticatedDataLength=" + authenticatedData.length + '}';
    }
}
