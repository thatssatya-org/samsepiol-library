package com.samsepiol.library.kafka.admin.offsets.impl;

import com.samsepiol.library.kafka.admin.offsets.OffsetMonitoringService;
import com.samsepiol.library.kafka.admin.offsets.models.response.PartitionOffsetInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultOffsetMonitoringService implements OffsetMonitoringService {
    private final KafkaAdmin kafkaAdmin;

    @Override
    public @NonNull Map<Integer, PartitionOffsetInfo> getOffsets(@NonNull String topic, @NonNull String groupId) {
        try (var adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            var committedOffsets = adminClient.listConsumerGroupOffsets(groupId)
                    .partitionsToOffsetAndMetadata().get();

            var topicPartitions = committedOffsets.keySet().stream()
                    .filter(tp -> tp.topic().equals(topic))
                    .toList();

            if (topicPartitions.isEmpty()) {
                return Collections.emptyMap();
            }

            var requestLatest = topicPartitions.stream()
                    .collect(Collectors.toMap(Function.identity(), tp -> OffsetSpec.latest()));

            var latestOffsets = adminClient.listOffsets(requestLatest).all().get();

            return topicPartitions.stream().collect(
                    Collectors.toMap(
                            TopicPartition::partition,
                            topicPartition -> PartitionOffsetInfo.builder()
                                    .currentOffset(committedOffsets.get(topicPartition).offset())
                                    .endOffset(latestOffsets.get(topicPartition).offset())
                                    .build()));


        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to fetch offsets for group " + groupId, e);
        }
    }

}
