package com.samsepiol.library.token.management;

import com.samsepiol.library.encryption.credential.CredentialDecryptionRequest;
import com.samsepiol.library.encryption.credential.CredentialEncryptionRequest;
import com.samsepiol.library.encryption.credential.CredentialEnvelopeCipher;
import com.samsepiol.library.core.security.management.ManagementAuthorizationBoundary;
import com.samsepiol.library.core.security.management.ManagementAuthorizationRequest;
import com.samsepiol.library.token.management.persistence.TokenRecordEntity;
import com.samsepiol.library.token.management.persistence.TokenRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Generic integration-token service. Callers own the reference and key selection; clients supply only token text.
 */
@RequiredArgsConstructor
@Service
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
            var envelope = envelopeCipher.encrypt(CredentialEncryptionRequest.builder()
                    .plaintext(plaintext).keyId(context.getKeyId()).authenticatedData(context.getReference().authenticatedData()).build());
            var record = TokenRecordEntity.from(context.getReference(), envelope);
            tokenRepository.upsert(record);
            return TokenWriteReceipt.from(context.getReference(), envelope);
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
        var record = tokenRepository.find(context.getReference());
        if (record == null) {
            throw new TokenNotFoundException(context.getReference());
        }
        if (!context.getReference().equals(record.reference())) {
            throw new IllegalStateException("Token repository returned a record outside the requested server-owned scope");
        }
        var plaintext = envelopeCipher.decrypt(CredentialDecryptionRequest.builder()
                .envelope(record.envelope()).authenticatedData(context.getReference().authenticatedData()).build());
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
