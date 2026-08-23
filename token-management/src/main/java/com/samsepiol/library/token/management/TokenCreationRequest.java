package com.samsepiol.library.token.management;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Intentionally minimal write input. A transport may deserialize only {@code token}; scope is assigned by its server.
 */
@Value
@Builder
@Jacksonized
@ToString
public class TokenCreationRequest {
    @NonNull @ToString.Exclude @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String token;

    /** Prevent accidental token emission from generic JSON loggers. */
    public char[] tokenCopy() {
        return token.toCharArray();
    }
}
