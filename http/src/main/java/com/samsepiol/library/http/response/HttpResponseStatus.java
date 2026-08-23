package com.samsepiol.library.http.response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class HttpResponseStatus {
    @NonNull Integer statusCode;

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
