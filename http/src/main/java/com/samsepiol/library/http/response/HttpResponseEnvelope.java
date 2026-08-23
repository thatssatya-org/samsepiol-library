package com.samsepiol.library.http.response;

import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * A bounded response captured once by the transport. Consumers may inspect status and headers such as ETag
 * without re-reading the underlying HTTP entity.
 */
@Value
public class HttpResponseEnvelope {
    int statusCode;
    @NonNull Map<String, List<String>> headers;
    @NonNull String body;

    public HttpResponseEnvelope(int statusCode, @NonNull Map<String, List<String>> headers, @NonNull String body) {
        this.statusCode = statusCode;
        this.headers = headers.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                entry -> entry.getKey().toLowerCase(Locale.ROOT), entry -> List.copyOf(entry.getValue())));
        this.body = body;
    }

    public @NonNull Optional<String> firstHeader(@NonNull String name) {
        return headers.getOrDefault(name.toLowerCase(Locale.ROOT), List.of()).stream().findFirst();
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
