package com.samsepiol.library.encryption.credential;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Non-secret audit metadata for a credential envelope key rotation.
 */
@Value
@Builder
@Jacksonized
@ToString
public class CredentialRotationMetadata {
    @NonNull String previousKeyId;
    @NonNull Long rotatedAtEpochMillis;
}
