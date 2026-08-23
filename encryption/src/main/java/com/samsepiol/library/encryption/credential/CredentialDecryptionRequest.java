package com.samsepiol.library.encryption.credential;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


/**
 * Ephemeral decryption input. Authenticated data must match the data used at encryption time.
 */
@Value
@Builder
@Jacksonized
@ToString
public class CredentialDecryptionRequest {
    @NonNull CredentialEnvelope envelope;
    @NonNull @Builder.Default @ToString.Exclude byte[] authenticatedData = new byte[0];
}
