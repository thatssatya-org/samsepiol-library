package com.samsepiol.library.core.security.management;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.core.exception.enums.Error;

/**
 * Raised when a management operation is not explicitly authorized.
 */
public final class ManagementAuthorizationDeniedException extends LibraryException {
    public ManagementAuthorizationDeniedException() {
        super(Error.MANAGEMENT_AUTHORIZATION);
    }
}
