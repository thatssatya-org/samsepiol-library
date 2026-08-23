package com.samsepiol.library.http.response;

import lombok.Value;

@Value
public class HttpResponseStatus {
    int statusCode;

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
