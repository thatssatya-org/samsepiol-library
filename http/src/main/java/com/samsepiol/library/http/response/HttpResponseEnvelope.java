package com.samsepiol.library.http.response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A bounded response captured once by the transport. Consumers may inspect status and headers such as ETag
 * without re-reading the underlying HTTP entity.
 */
@Value
@Builder
public class HttpResponseEnvelope<T> {
    @NonNull Integer statusCode;
    @NonNull Map<String, List<String>> headers;
    T body;

    public @NonNull Optional<String> firstHeader(@NonNull String name) {
        return headers.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .flatMap(entry -> entry.getValue().stream())
                .findFirst();
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
