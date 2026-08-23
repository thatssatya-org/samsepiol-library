package com.samsepiol.library.token.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.samsepiol.library.core.security.credential.AesGcmCredentialEnvelopeCipher;
import com.samsepiol.library.core.security.credential.CredentialCryptographyException;
import com.samsepiol.library.core.security.credential.CredentialDecryptionRequest;
import com.samsepiol.library.core.security.management.ManagementAuthorizationBoundary;
import com.samsepiol.library.core.security.management.ManagementAuthorizationRequest;
import com.samsepiol.library.token.management.persistence.TokenRecord;
import com.samsepiol.library.token.management.persistence.TokenRepository;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        var request = new TokenCreationRequest(TOKEN);

        var receipt = service.create(request, context, authorization());
        var persisted = repository.find(context.reference()).orElseThrow();

        assertEquals(context.reference(), receipt.reference());
        assertEquals(context.reference(), persisted.reference());
        assertFalse(new String(persisted.getCiphertext(), StandardCharsets.UTF_8).contains(TOKEN));
        assertFalse(persisted.toString().contains(TOKEN));
        assertFalse(new ObjectMapper().writeValueAsString(persisted).contains(TOKEN));
        assertThrows(InvalidDefinitionException.class, () -> new ObjectMapper().writeValueAsString(request));
        assertTrue(request.toString().contains("[REDACTED]"));
        assertArrayEquals(TOKEN.toCharArray(), request.tokenCopy());
    }

    @Test
    void bindsCiphertextToTheServerControlledReference() {
        var repository = new InMemoryTokenRepository();
        var cipher = cipher();
        var service = new DefaultTokenManagementService(cipher, repository, request -> { });
        var github = context("github", "thatssatya", "fine-grained-pat");
        service.create(new TokenCreationRequest(TOKEN), github, authorization());
        var envelope = repository.find(github.reference()).orElseThrow().envelope();
        var differentScope = context("github", "other-subject", "fine-grained-pat");

        assertThrows(CredentialCryptographyException.class,
                () -> cipher.decrypt(new CredentialDecryptionRequest(envelope, differentScope.reference().authenticatedData())));
        assertDoesNotThrow(() -> cipher.decrypt(new CredentialDecryptionRequest(envelope, github.reference().authenticatedData())));
    }

    @Test
    void exposesTokenOnlyInsideAnEphemeralInternalCallbackAndWipesItAfterward() {
        var repository = new InMemoryTokenRepository();
        var authorizationCalls = new AtomicInteger();
        var service = service(repository, request -> authorizationCalls.incrementAndGet());
        var context = context("github", "thatssatya", "fine-grained-pat");
        service.create(new TokenCreationRequest(TOKEN), context, authorization());
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
        return new AesGcmCredentialEnvelopeCipher(keyId -> key);
    }

    private static TokenStorageContext context(String namespace, String subject, String name) {
        return new TokenStorageContext(new TokenReference(namespace, subject, name), "integration-token-key-v1");
    }

    private static ManagementAuthorizationRequest authorization() {
        return new ManagementAuthorizationRequest("tailnet-operator", "token-management", Map.of("source", "tailnet"));
    }

    private static final class InMemoryTokenRepository implements TokenRepository {
        private final Map<TokenReference, TokenRecord> records = new HashMap<>();

        @Override
        public Optional<TokenRecord> find(TokenReference reference) {
            return Optional.ofNullable(records.get(reference));
        }

        @Override
        public TokenRecord upsert(TokenRecord record) {
            records.put(record.reference(), record);
            return record;
        }
    }
}
