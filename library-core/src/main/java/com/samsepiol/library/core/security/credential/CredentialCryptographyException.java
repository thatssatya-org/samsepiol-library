package com.samsepiol.library.core.security.credential;

/**
 * Raised when credential encryption, decryption, integrity validation, or key resolution fails.
 */
public final class CredentialCryptographyException extends RuntimeException {
    public CredentialCryptographyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialCryptographyException(String message) {
        super(message);
    }
}
