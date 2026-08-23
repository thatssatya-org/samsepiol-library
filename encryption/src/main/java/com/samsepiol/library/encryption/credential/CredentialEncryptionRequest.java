package com.samsepiol.library.encryption.credential;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


/**
 * Ephemeral encryption input. Do not persist or log instances of this type.
 */
@Value
@Builder
@Jacksonized
@ToString
public class CredentialEncryptionRequest {
    @NonNull @ToString.Exclude byte[] plaintext;
    @NonNull String keyId;
    @NonNull @Builder.Default @ToString.Exclude byte[] authenticatedData = new byte[0];
}
