package com.samsepiol.library.encryption.credential;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AesGcmCredentialEnvelopeCipherTest {
    private static final byte[] PLAINTEXT = "github-pat-that-must-never-persist".getBytes(StandardCharsets.UTF_8);
    private static final byte[] AUTHENTICATED_DATA = "owner:integration:credential".getBytes(StandardCharsets.UTF_8);
    private static final SecretKeySpec KEY_ONE = new SecretKeySpec(new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32
    }, "AES");
    private static final SecretKeySpec KEY_TWO = new SecretKeySpec(new byte[]{
            32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17,
            16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1
    }, "AES");

    @Test
    void encryptsDecryptsAndKeepsPlaintextOutOfThePersistedEnvelope() throws Exception {
        var cipher = cipher(Map.of("primary", KEY_ONE));
        var envelope = cipher.encrypt(encryptionRequest(PLAINTEXT, "primary", AUTHENTICATED_DATA));

        assertArrayEquals(PLAINTEXT, cipher.decrypt(decryptionRequest(envelope, AUTHENTICATED_DATA)));
        assertNotEquals(new String(PLAINTEXT, StandardCharsets.UTF_8), new String(envelope.getCiphertext(), StandardCharsets.UTF_8));

        var serialized = new ObjectMapper().writeValueAsString(envelope);
        assertFalse(serialized.contains(new String(PLAINTEXT, StandardCharsets.UTF_8)));
        assertFalse(envelope.toString().contains(new String(PLAINTEXT, StandardCharsets.UTF_8)));
        assertFalse(encryptionRequest(PLAINTEXT, "primary", AUTHENTICATED_DATA)
                .toString().contains(new String(PLAINTEXT, StandardCharsets.UTF_8)));
    }

    @Test
    void rejectsTamperedCiphertextAndWrongKeyMaterial() {
        var sourceCipher = cipher(Map.of("primary", KEY_ONE));
        var envelope = sourceCipher.encrypt(encryptionRequest(PLAINTEXT, "primary", AUTHENTICATED_DATA));
        var tamperedCiphertext = envelope.getCiphertext();
        tamperedCiphertext[0] ^= 1;
        var tampered = CredentialEnvelope.builder().envelopeVersion(envelope.getEnvelopeVersion()).algorithm(envelope.getAlgorithm())
                .keyId(envelope.getKeyId()).nonce(envelope.getNonce()).ciphertext(tamperedCiphertext)
                .encryptedAtEpochMillis(envelope.getEncryptedAtEpochMillis()).rotationMetadata(envelope.getRotationMetadata()).build();

        assertThrows(CredentialCryptographyException.class,
                () -> sourceCipher.decrypt(decryptionRequest(tampered, AUTHENTICATED_DATA)));
        assertThrows(CredentialCryptographyException.class,
                () -> cipher(Map.of("primary", KEY_TWO))
                        .decrypt(decryptionRequest(envelope, AUTHENTICATED_DATA)));
        assertThrows(CredentialCryptographyException.class,
                () -> sourceCipher.decrypt(decryptionRequest(envelope, "different-context".getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    void rejectsTamperedMetadataAndRotatesWithoutExposingPlaintext() {
        var cipher = cipher(Map.of("primary", KEY_ONE, "rotated", KEY_TWO));
        var envelope = cipher.encrypt(encryptionRequest(PLAINTEXT, "primary", AUTHENTICATED_DATA));
        var metadataTampered = CredentialEnvelope.builder().envelopeVersion(envelope.getEnvelopeVersion()).algorithm(envelope.getAlgorithm())
                .keyId("rotated").nonce(envelope.getNonce()).ciphertext(envelope.getCiphertext())
                .encryptedAtEpochMillis(envelope.getEncryptedAtEpochMillis()).rotationMetadata(envelope.getRotationMetadata()).build();

        assertThrows(CredentialCryptographyException.class,
                () -> cipher.decrypt(decryptionRequest(metadataTampered, AUTHENTICATED_DATA)));

        var rotated = cipher.rotate(decryptionRequest(envelope, AUTHENTICATED_DATA), "rotated");
        assertArrayEquals(PLAINTEXT, cipher.decrypt(decryptionRequest(rotated, AUTHENTICATED_DATA)));
        assertTrue(rotated.getRotationMetadata().getPreviousKeyId().equals("primary"));
        assertDoesNotThrow(() -> Arrays.fill(rotated.getCiphertext(), (byte) 0));
    }

    private AesGcmCredentialEnvelopeCipher cipher(Map<String, SecretKeySpec> keys) {
        return new AesGcmCredentialEnvelopeCipher(keyId -> keys.get(keyId), System::currentTimeMillis);
    }

    private CredentialEncryptionRequest encryptionRequest(byte[] plaintext, String keyId, byte[] authenticatedData) {
        return CredentialEncryptionRequest.builder().plaintext(plaintext).keyId(keyId).authenticatedData(authenticatedData).build();
    }

    private CredentialDecryptionRequest decryptionRequest(CredentialEnvelope envelope, byte[] authenticatedData) {
        return CredentialDecryptionRequest.builder().envelope(envelope).authenticatedData(authenticatedData).build();
    }
}
