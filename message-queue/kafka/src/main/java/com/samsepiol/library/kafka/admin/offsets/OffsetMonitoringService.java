package com.samsepiol.library.kafka.admin.offsets;

import com.samsepiol.library.kafka.admin.offsets.models.response.PartitionOffsetInfo;
import lombok.NonNull;

import java.util.Map;

public interface OffsetMonitoringService {

    @NonNull
    Map<Integer, PartitionOffsetInfo> getOffsets(@NonNull String topic, @NonNull String groupId);
}
