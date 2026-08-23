package com.samsepiol.library.core.security.credential;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.LongSupplier;

/**
 * JCA AES-GCM implementation of {@link CredentialEnvelopeCipher}.
 */
public final class AesGcmCredentialEnvelopeCipher implements CredentialEnvelopeCipher {
    public static final int ENVELOPE_VERSION = 1;
    public static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int NONCE_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final CredentialKeyResolver keyResolver;
    private final SecureRandom secureRandom;
    private final LongSupplier clock;

    public AesGcmCredentialEnvelopeCipher(CredentialKeyResolver keyResolver) {
        this(keyResolver, new SecureRandom(), System::currentTimeMillis);
    }

    public AesGcmCredentialEnvelopeCipher(
            CredentialKeyResolver keyResolver,
            SecureRandom secureRandom,
            LongSupplier clock) {
        this.keyResolver = Objects.requireNonNull(keyResolver, "keyResolver must not be null");
        this.secureRandom = Objects.requireNonNull(secureRandom, "secureRandom must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public CredentialEnvelope encrypt(CredentialEncryptionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        var nonce = new byte[NONCE_LENGTH_BYTES];
        secureRandom.nextBytes(nonce);
        var encryptedAt = currentTime();
        var envelope = new CredentialEnvelope(
                ENVELOPE_VERSION,
                ALGORITHM,
                request.getKeyId(),
                nonce,
                encrypt(resolveKey(request.getKeyId()), nonce, request.getPlaintext(), request.getAuthenticatedData(),
                        ENVELOPE_VERSION, ALGORITHM, request.getKeyId()),
                encryptedAt,
                null);
        Arrays.fill(nonce, (byte) 0);
        return envelope;
    }

    @Override
    public byte[] decrypt(CredentialDecryptionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        var envelope = request.getEnvelope();
        validateEnvelope(envelope);
        return decrypt(resolveKey(envelope.getKeyId()), envelope.getNonce(), envelope.getCiphertext(),
                request.getAuthenticatedData(), envelope.getEnvelopeVersion(), envelope.getAlgorithm(), envelope.getKeyId());
    }

    @Override
    public CredentialEnvelope rotate(CredentialDecryptionRequest request, String targetKeyId) {
        Objects.requireNonNull(request, "request must not be null");
        if (targetKeyId == null || targetKeyId.isBlank()) {
            throw new IllegalArgumentException("targetKeyId must not be blank");
        }
        var plaintext = decrypt(request);
        try {
            var replacement = encrypt(new CredentialEncryptionRequest(plaintext, targetKeyId, request.getAuthenticatedData()));
            return new CredentialEnvelope(
                    replacement.getEnvelopeVersion(),
                    replacement.getAlgorithm(),
                    replacement.getKeyId(),
                    replacement.getNonce(),
                    replacement.getCiphertext(),
                    replacement.getEncryptedAtEpochMillis(),
                    new CredentialRotationMetadata(request.getEnvelope().getKeyId(), currentTime()));
        } finally {
            Arrays.fill(plaintext, (byte) 0);
        }
    }

    private byte[] encrypt(SecretKey key, byte[] nonce, byte[] plaintext, byte[] authenticatedData,
                           int envelopeVersion, String algorithm, String keyId) {
        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            applyAuthenticatedData(cipher, authenticatedData, envelopeVersion, algorithm, keyId);
            return cipher.doFinal(plaintext);
        } catch (GeneralSecurityException exception) {
            throw new CredentialCryptographyException("Credential encryption failed", exception);
        }
    }

    private byte[] decrypt(SecretKey key, byte[] nonce, byte[] ciphertext, byte[] authenticatedData,
                           int envelopeVersion, String algorithm, String keyId) {
        try {
            var cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            applyAuthenticatedData(cipher, authenticatedData, envelopeVersion, algorithm, keyId);
            return cipher.doFinal(ciphertext);
        } catch (GeneralSecurityException exception) {
            throw new CredentialCryptographyException("Credential decryption failed", exception);
        }
    }

    private void applyAuthenticatedData(Cipher cipher, byte[] authenticatedData,
                                        int envelopeVersion, String algorithm, String keyId) {
        cipher.updateAAD((envelopeVersion + "|" + algorithm + "|" + keyId).getBytes(StandardCharsets.UTF_8));
        if (authenticatedData.length > 0) {
            cipher.updateAAD(authenticatedData);
        }
    }

    private SecretKey resolveKey(String keyId) {
        try {
            var key = keyResolver.resolve(keyId);
            if (key == null || !"AES".equalsIgnoreCase(key.getAlgorithm())) {
                throw new CredentialCryptographyException("Credential key resolver returned an invalid key");
            }
            return key;
        } catch (CredentialCryptographyException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new CredentialCryptographyException("Credential key resolution failed", exception);
        }
    }

    private void validateEnvelope(CredentialEnvelope envelope) {
        if (envelope.getEnvelopeVersion() != ENVELOPE_VERSION) {
            throw new CredentialCryptographyException("Unsupported credential envelope version");
        }
        if (!ALGORITHM.equals(envelope.getAlgorithm())) {
            throw new CredentialCryptographyException("Unsupported credential envelope algorithm");
        }
        if (envelope.getNonce().length != NONCE_LENGTH_BYTES) {
            throw new CredentialCryptographyException("Invalid credential envelope nonce");
        }
    }

    private long currentTime() {
        var value = clock.getAsLong();
        if (value <= 0) {
            throw new CredentialCryptographyException("Credential clock returned an invalid value");
        }
        return value;
    }
}
