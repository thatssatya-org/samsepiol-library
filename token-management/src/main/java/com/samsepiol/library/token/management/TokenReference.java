package com.samsepiol.library.token.management;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * A server-owned logical address for one integration token. Do not construct this type from an HTTP route or body.
 */
public record TokenReference(String namespace, String subject, String name) {
    public TokenReference {
        namespace = required(namespace, "namespace");
        subject = required(subject, "subject");
        name = required(name, "name");
    }

    byte[] authenticatedData() {
        return ("token-management:v1|" + component(namespace) + component(subject) + component(name))
                .getBytes(StandardCharsets.UTF_8);
    }

    /** Stable opaque storage identifier; it is derived from server-owned scope, never token material. */
    public String persistentId() {
        try {
            var digest = MessageDigest.getInstance("SHA-256")
                    .digest((namespace + '\u0000' + subject + '\u0000' + name).getBytes(StandardCharsets.UTF_8));
            var result = new StringBuilder("TM_");
            for (byte value : digest) {
                result.append(String.format("%02x", value));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static String component(String value) {
        return value.length() + ":" + value + "|";
    }

    private static String required(String value, String field) {
        var result = Objects.requireNonNull(value, field + " must not be null");
        if (result.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return result;
    }
}
