package com.samsepiol.library.token.management;

import com.samsepiol.library.core.security.management.ManagementAuthorizationRequest;
import lombok.NonNull;

public interface TokenManagementService {
    <T> T useForInternalIntegration(@NonNull TokenStorageContext context,
                                    @NonNull ManagementAuthorizationRequest authorizationRequest,
                                    @NonNull TokenUse<T> tokenUse);
}
