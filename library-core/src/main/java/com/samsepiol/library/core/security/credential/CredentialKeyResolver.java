package com.samsepiol.library.core.security.credential;

import javax.crypto.SecretKey;

/**
 * Resolves key material by opaque identifier. Implementations may use a KMS, HSM, or a local key source.
 */
@FunctionalInterface
public interface CredentialKeyResolver {
    SecretKey resolve(String keyId);
}
