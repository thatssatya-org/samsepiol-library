package com.samsepiol.library.token.management;

import com.samsepiol.library.encryption.credential.CredentialEnvelope;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/** Safe write acknowledgement. It contains no token material. */
@Value
@Builder
@ToString
public class TokenWriteReceipt {
    @NonNull TokenReference reference;
    @NonNull String keyId;
    @NonNull Long encryptedAtEpochMillis;

    static TokenWriteReceipt from(TokenReference reference, CredentialEnvelope envelope) {
        return TokenWriteReceipt.builder().reference(reference).keyId(envelope.getKeyId())
                .encryptedAtEpochMillis(envelope.getEncryptedAtEpochMillis()).build();
    }
}
