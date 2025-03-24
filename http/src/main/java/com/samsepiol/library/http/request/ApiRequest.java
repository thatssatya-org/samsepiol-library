package com.samsepiol.library.http.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.Map;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ApiRequest {
    @NonNull
    String service;

    @NonNull
    String api;

    Object body;

    @NonNull
    @Builder.Default
    Map<String, String> headers = Collections.emptyMap();
}
