package com.samsepiol.library.core.security.management;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ManagementAuthorizationBoundaryTest {
    private final ManagementAuthorizationRequest request = new ManagementAuthorizationRequest(
            "operator-123", "credential.write", Map.of("network", "private"));

    @Test
    void deniesByDefault() {
        var authorization = new DefaultDenyManagementAuthorization();

        assertThrows(ManagementAuthorizationDeniedException.class, () -> authorization.requireAuthorized(request));
    }

    @Test
    void allowsOnlyWhenApplicationPolicyExplicitlyApprovesTheOperation() {
        var authorization = new PolicyBackedManagementAuthorization(candidate -> candidate.getPrincipalId().equals("operator-123")
                && candidate.getOperation().equals("credential.write")
                && candidate.getAttributes().get("network").equals("private"));

        assertDoesNotThrow(() -> authorization.requireAuthorized(request));
        assertThrows(ManagementAuthorizationDeniedException.class,
                () -> authorization.requireAuthorized(new ManagementAuthorizationRequest(
                        "operator-123", "credential.read", Map.of("network", "private"))));
    }
}
