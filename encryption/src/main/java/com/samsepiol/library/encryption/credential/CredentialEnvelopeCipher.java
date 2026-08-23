package com.samsepiol.library.encryption.credential;

/**
 * Opaque encryption port for persistable credentials. Implementations must provide authenticated encryption.
 */
public interface CredentialEnvelopeCipher {
    CredentialEnvelope encrypt(CredentialEncryptionRequest request);

    byte[] decrypt(CredentialDecryptionRequest request);

    CredentialEnvelope rotate(CredentialDecryptionRequest request, String targetKeyId);
}
