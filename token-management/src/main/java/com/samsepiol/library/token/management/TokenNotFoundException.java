package com.samsepiol.library.token.management;

public final class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(TokenReference reference) {
        super("No token exists for server-owned reference " + reference);
    }
}
