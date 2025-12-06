package com.samsepiol.message.queue.core.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.Map;

@Value
@Builder
public class MessageHandlerRequest {
    @NonNull
    String key;
    @NonNull
    String value;
    @NonNull
    @Builder.Default
    Map<String, String> headers = Collections.emptyMap();
}
