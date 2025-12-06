package com.samsepiol.library.kafka.admin.offsets.models.response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class PartitionOffsetInfo {
    @NonNull
    Long currentOffset;
    @NonNull
    Long endOffset;
}