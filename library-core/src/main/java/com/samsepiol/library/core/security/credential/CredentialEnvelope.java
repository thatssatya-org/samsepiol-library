package com.samsepiol.library.core.security.credential;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * Persistable, versioned encrypted credential material. This type deliberately has no plaintext field.
 */
@Getter
@EqualsAndHashCode
public final class CredentialEnvelope {
    private final int envelopeVersion;
    private final String algorithm;
    private final String keyId;
    private final byte[] nonce;
    private final byte[] ciphertext;
    private final long encryptedAtEpochMillis;
    private final CredentialRotationMetadata rotationMetadata;

    @JsonCreator
    public CredentialEnvelope(
            @JsonProperty("envelopeVersion") int envelopeVersion,
            @JsonProperty("algorithm") String algorithm,
            @JsonProperty("keyId") String keyId,
            @JsonProperty("nonce") byte[] nonce,
            @JsonProperty("ciphertext") byte[] ciphertext,
            @JsonProperty("encryptedAtEpochMillis") long encryptedAtEpochMillis,
            @JsonProperty("rotationMetadata") CredentialRotationMetadata rotationMetadata) {
        if (envelopeVersion <= 0) {
            throw new IllegalArgumentException("envelopeVersion must be positive");
        }
        if (encryptedAtEpochMillis <= 0) {
            throw new IllegalArgumentException("encryptedAtEpochMillis must be positive");
        }
        this.envelopeVersion = envelopeVersion;
        this.algorithm = requireText(algorithm, "algorithm");
        this.keyId = requireText(keyId, "keyId");
        this.nonce = copyRequired(nonce, "nonce");
        this.ciphertext = copyRequired(ciphertext, "ciphertext");
        this.encryptedAtEpochMillis = encryptedAtEpochMillis;
        this.rotationMetadata = rotationMetadata;
    }

    public byte[] getNonce() {
        return Arrays.copyOf(nonce, nonce.length);
    }

    public byte[] getCiphertext() {
        return Arrays.copyOf(ciphertext, ciphertext.length);
    }

    @Override
    public String toString() {
        return "CredentialEnvelope{"
                + "envelopeVersion=" + envelopeVersion
                + ", algorithm='" + algorithm + '\''
                + ", keyId='" + keyId + '\''
                + ", nonceLength=" + nonce.length
                + ", ciphertextLength=" + ciphertext.length
                + ", encryptedAtEpochMillis=" + encryptedAtEpochMillis
                + ", rotationMetadata=" + rotationMetadata
                + '}';
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
