package com.samsepiol.library.encryption.credential;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.core.exception.enums.Error;

/**
 * Raised when credential encryption, decryption, integrity validation, or key resolution fails.
 */
public final class CredentialCryptographyException extends LibraryException {
    public CredentialCryptographyException(String message, Throwable cause) {
        super(Error.CREDENTIAL_CRYPTOGRAPHY);
        initCause(cause);
    }

    public CredentialCryptographyException(String message) {
        super(Error.CREDENTIAL_CRYPTOGRAPHY);
    }
}
