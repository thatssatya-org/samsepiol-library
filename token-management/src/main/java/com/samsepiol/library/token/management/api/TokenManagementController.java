package com.samsepiol.library.token.management.api;

import com.samsepiol.library.token.management.DefaultTokenManagementService;
import com.samsepiol.library.token.management.TokenCreationRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Write-only management endpoint. It deliberately returns no token or persistence metadata.
 */
@RestController
@RequestMapping("/api/v1/token-management/tokens")
@RequiredArgsConstructor
public class TokenManagementController {
    private final @NonNull DefaultTokenManagementService tokenManagementService;
    private final @NonNull TokenManagementRequestContextResolver requestContextResolver;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @NonNull TokenCreationRequest request) {
        var context = requestContextResolver.resolve();
        tokenManagementService.create(request, context.getStorageContext(), context.getAuthorizationRequest());
        return ResponseEntity.noContent().build();
    }
}
