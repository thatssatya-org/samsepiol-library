package com.samsepiol.library.token.management;

import com.samsepiol.library.core.security.credential.CredentialDecryptionRequest;
import com.samsepiol.library.core.security.credential.CredentialEncryptionRequest;
import com.samsepiol.library.core.security.credential.CredentialEnvelopeCipher;
import com.samsepiol.library.core.security.management.ManagementAuthorizationBoundary;
import com.samsepiol.library.core.security.management.ManagementAuthorizationRequest;
import com.samsepiol.library.token.management.persistence.TokenRecord;
import com.samsepiol.library.token.management.persistence.TokenRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Generic integration-token service. Callers own the reference and key selection; clients supply only token text.
 */
@RequiredArgsConstructor
public class DefaultTokenManagementService implements TokenManagementService {
    private final CredentialEnvelopeCipher envelopeCipher;
    private final TokenRepository tokenRepository;
    private final ManagementAuthorizationBoundary authorizationBoundary;

    @Override
    public @NonNull TokenWriteReceipt create(@NonNull TokenCreationRequest request, @NonNull TokenStorageContext context,
                                             @NonNull ManagementAuthorizationRequest authorizationRequest) {
        authorizationBoundary.requireAuthorized(authorizationRequest);
        var token = request.tokenCopy();
        byte[] plaintext = null;
        try {
            plaintext = new String(token).getBytes(StandardCharsets.UTF_8);
            var envelope = envelopeCipher.encrypt(new CredentialEncryptionRequest(
                    plaintext, context.keyId(), context.reference().authenticatedData()));
            var record = TokenRecord.from(context.reference(), envelope);
            tokenRepository.upsert(record);
            return TokenWriteReceipt.from(context.reference(), envelope);
        } finally {
            Arrays.fill(token, '\0');
            if (plaintext != null) {
                Arrays.fill(plaintext, (byte) 0);
            }
        }
    }

    @Override
    public <T> T useForInternalIntegration(@NonNull TokenStorageContext context,
                                           @NonNull ManagementAuthorizationRequest authorizationRequest,
                                           @NonNull TokenUse<T> tokenUse) {
        authorizationBoundary.requireAuthorized(authorizationRequest);
        var record = tokenRepository.find(context.reference())
                .orElseThrow(() -> new TokenNotFoundException(context.reference()));
        if (!context.reference().equals(record.reference())) {
            throw new IllegalStateException("Token repository returned a record outside the requested server-owned scope");
        }
        var plaintext = envelopeCipher.decrypt(new CredentialDecryptionRequest(
                record.envelope(), context.reference().authenticatedData()));
        char[] token = null;
        try {
            token = new String(plaintext, StandardCharsets.UTF_8).toCharArray();
            return tokenUse.use(token);
        } finally {
            Arrays.fill(plaintext, (byte) 0);
            if (token != null) {
                Arrays.fill(token, '\0');
            }
        }
    }
}
