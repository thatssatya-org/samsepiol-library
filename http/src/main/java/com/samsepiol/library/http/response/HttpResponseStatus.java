package com.samsepiol.library.http.response;

import lombok.NonNull;
import lombok.Value;

@Value
public class HttpResponseStatus {
    @NonNull Integer statusCode;

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
