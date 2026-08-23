package com.samsepiol.library.token.management;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Intentionally minimal write input. A transport may deserialize only {@code token}; scope is assigned by its server.
 */
public final class TokenCreationRequest {
    private final char[] token;

    @JsonCreator
    public TokenCreationRequest(@JsonProperty("token") String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token must not be blank");
        }
        this.token = token.toCharArray();
    }

    /** Prevent accidental token emission from generic JSON loggers. */
    @JsonIgnore
    public char[] tokenCopy() {
        return Arrays.copyOf(token, token.length);
    }

    @Override
    public String toString() {
        return "TokenCreationRequest{token=[REDACTED]}";
    }
}
