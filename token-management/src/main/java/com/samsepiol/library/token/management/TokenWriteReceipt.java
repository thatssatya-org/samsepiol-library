package com.samsepiol.library.token.management;

import com.samsepiol.library.core.security.credential.CredentialEnvelope;

import java.util.Objects;

/** Safe write acknowledgement. It contains no token material. */
public record TokenWriteReceipt(TokenReference reference, String keyId, long encryptedAtEpochMillis) {
    static TokenWriteReceipt from(TokenReference reference, CredentialEnvelope envelope) {
        Objects.requireNonNull(reference, "reference must not be null");
        Objects.requireNonNull(envelope, "envelope must not be null");
        return new TokenWriteReceipt(reference, envelope.getKeyId(), envelope.getEncryptedAtEpochMillis());
    }
}
