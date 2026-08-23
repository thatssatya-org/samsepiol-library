package com.samsepiol.library.encryption.credential;

import java.util.Arrays;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


/**
 * Persistable, versioned encrypted credential material. This type deliberately has no plaintext field.
 */
@Value
@Builder
@Jacksonized
@ToString
public class CredentialEnvelope {
    @NonNull Integer envelopeVersion;
    @NonNull String algorithm;
    @NonNull String keyId;
    @NonNull @ToString.Exclude byte[] nonce;
    @NonNull @ToString.Exclude byte[] ciphertext;
    @NonNull Long encryptedAtEpochMillis;
    CredentialRotationMetadata rotationMetadata;

    public byte[] getNonce() {
        return Arrays.copyOf(nonce, nonce.length);
    }

    public byte[] getCiphertext() {
        return Arrays.copyOf(ciphertext, ciphertext.length);
    }
}
