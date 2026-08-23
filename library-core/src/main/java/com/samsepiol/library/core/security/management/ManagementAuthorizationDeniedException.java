package com.samsepiol.library.core.security.management;

/**
 * Raised when a management operation is not explicitly authorized.
 */
public final class ManagementAuthorizationDeniedException extends SecurityException {
    public ManagementAuthorizationDeniedException() {
        super("Management operation is not authorized");
    }
}
