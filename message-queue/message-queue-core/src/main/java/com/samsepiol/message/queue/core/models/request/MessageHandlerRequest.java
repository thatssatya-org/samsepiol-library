package com.samsepiol.message.queue.core.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class MessageHandlerRequest {
    @NonNull
    String key;
    @NonNull
    String value;
}
