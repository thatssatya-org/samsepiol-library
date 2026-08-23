package com.samsepiol.library.token.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samsepiol.library.encryption.credential.AesGcmCredentialEnvelopeCipher;
import com.samsepiol.library.encryption.credential.CredentialCryptographyException;
import com.samsepiol.library.encryption.credential.CredentialDecryptionRequest;
import com.samsepiol.library.core.security.management.ManagementAuthorizationBoundary;
import com.samsepiol.library.core.security.management.ManagementAuthorizationRequest;
import com.samsepiol.library.token.management.persistence.TokenRecordEntity;
import com.samsepiol.library.token.management.persistence.TokenRepository;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultTokenManagementServiceTest {
    private static final String TOKEN = "ghp_super_secret_for_tests";

    @Test
    void persistsOnlyAnEncryptedEnvelopeAndNeverSerializesPlaintext() throws Exception {
        var repository = new InMemoryTokenRepository();
        var service = service(repository, request -> { });
        var context = context("github", "thatssatya", "fine-grained-pat");
        var request = TokenCreationRequest.builder().token(TOKEN).build();

        var receipt = service.create(request, context, authorization());
        var persisted = repository.find(context.getReference());

        assertEquals(context.getReference(), receipt.getReference());
        assertEquals(context.getReference(), persisted.reference());
        assertFalse(persisted.getCiphertextBase64().contains(TOKEN));
        assertFalse(persisted.toString().contains(TOKEN));
        assertFalse(new ObjectMapper().writeValueAsString(persisted).contains(TOKEN));
        assertFalse(new ObjectMapper().writeValueAsString(request).contains(TOKEN));
        assertFalse(request.toString().contains(TOKEN));
        assertArrayEquals(TOKEN.toCharArray(), request.tokenCopy());
    }

    @Test
    void bindsCiphertextToTheServerControlledReference() {
        var repository = new InMemoryTokenRepository();
        var cipher = cipher();
        var service = new DefaultTokenManagementService(cipher, repository, request -> { });
        var github = context("github", "thatssatya", "fine-grained-pat");
        service.create(TokenCreationRequest.builder().token(TOKEN).build(), github, authorization());
        var envelope = repository.find(github.getReference()).envelope();
        var differentScope = context("github", "other-subject", "fine-grained-pat");

        assertThrows(CredentialCryptographyException.class,
                () -> cipher.decrypt(CredentialDecryptionRequest.builder().envelope(envelope)
                        .authenticatedData(differentScope.getReference().authenticatedData()).build()));
        assertDoesNotThrow(() -> cipher.decrypt(CredentialDecryptionRequest.builder().envelope(envelope)
                .authenticatedData(github.getReference().authenticatedData()).build()));
    }

    @Test
    void exposesTokenOnlyInsideAnEphemeralInternalCallbackAndWipesItAfterward() {
        var repository = new InMemoryTokenRepository();
        var authorizationCalls = new AtomicInteger();
        var service = service(repository, request -> authorizationCalls.incrementAndGet());
        var context = context("github", "thatssatya", "fine-grained-pat");
        service.create(TokenCreationRequest.builder().token(TOKEN).build(), context, authorization());
        var supplied = new AtomicReference<char[]>();

        var outcome = service.useForInternalIntegration(context, authorization(), token -> {
            supplied.set(token);
            return new String(token);
        });

        assertEquals(TOKEN, outcome);
        assertArrayEquals(new char[supplied.get().length], supplied.get());
        assertEquals(2, authorizationCalls.get());
        assertFalse(Arrays.stream(TokenManagementService.class.getMethods())
                .map(method -> method.getName().toLowerCase())
                .anyMatch(name -> name.startsWith("get") || name.startsWith("find") || name.startsWith("list")));
    }

    @Test
    void tokenCreationInputContainsNoClientControlledScopeFields() {
        assertEquals(1, TokenCreationRequest.class.getDeclaredFields().length);
        assertEquals("token", TokenCreationRequest.class.getDeclaredFields()[0].getName());
    }

    private static DefaultTokenManagementService service(TokenRepository repository,
                                                          ManagementAuthorizationBoundary boundary) {
        return new DefaultTokenManagementService(cipher(), repository, boundary);
    }

    private static AesGcmCredentialEnvelopeCipher cipher() {
        var key = new SecretKeySpec(new byte[32], "AES");
        return new AesGcmCredentialEnvelopeCipher(keyId -> key, System::currentTimeMillis);
    }

    private static TokenStorageContext context(String namespace, String subject, String name) {
        return TokenStorageContext.required(new TokenReference(namespace, subject, name), "integration-token-key-v1");
    }

    private static ManagementAuthorizationRequest authorization() {
        return ManagementAuthorizationRequest.builder().principalId("tailnet-operator").operation("token-management")
                .attributes(Map.of("source", "tailnet")).build();
    }

    private static final class InMemoryTokenRepository implements TokenRepository {
        private final Map<TokenReference, TokenRecordEntity> records = new HashMap<>();

        @Override
        public TokenRecordEntity find(TokenReference reference) {
            return records.get(reference);
        }

        @Override
        public TokenRecordEntity upsert(TokenRecordEntity record) {
            records.put(record.reference(), record);
            return record;
        }
    }
}
