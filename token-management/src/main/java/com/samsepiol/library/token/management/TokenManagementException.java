package com.samsepiol.library.token.management;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.core.exception.enums.Error;

public class TokenManagementException extends LibraryException {
    public TokenManagementException() {
        super(Error.TOKEN_MANAGEMENT);
    }
}
