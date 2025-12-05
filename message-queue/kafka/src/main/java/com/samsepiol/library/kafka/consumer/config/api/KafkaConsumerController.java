package com.samsepiol.library.kafka.consumer.config.api;

import com.samsepiol.library.kafka.admin.offsets.OffsetMonitoringService;
import com.samsepiol.library.kafka.admin.offsets.models.response.PartitionOffsetInfo;
import com.samsepiol.library.kafka.consumer.config.service.ConsumerConfigService;
import com.samsepiol.library.kafka.consumer.config.service.repo.models.ConsumerConfigEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consumers/kafka")
@RequiredArgsConstructor
@ConditionalOnProperty("spring.kafka.bootstrap-servers")
public class KafkaConsumerController {
    private final ConsumerConfigService consumerConfigService;
    private final OffsetMonitoringService offsetMonitoringService;

    @GetMapping("/active")
    public ResponseEntity<List<ConsumerConfigEntity>> fetchActive() {
        var activeConsumers = consumerConfigService.findActive();
        return ResponseEntity.ok(activeConsumers);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<ConsumerConfigEntity>> fetchInactive() {
        var inactiveConsumers = consumerConfigService.findInactive();
        return ResponseEntity.ok(inactiveConsumers);
    }

    @GetMapping("/{groupId}/{topic}/offsets")
    public ResponseEntity<Map<Integer, PartitionOffsetInfo>> fetchOffsets(@PathVariable String groupId,
                                                                          @PathVariable String topic) {
        return ResponseEntity.ok(offsetMonitoringService.getOffsets(topic, groupId));
    }

    @PostMapping
    public ResponseEntity<ConsumerConfigEntity> create(@RequestBody ConsumerConfigEntity consumerConfigEntity) {
        var consumer = consumerConfigService.insert(consumerConfigEntity);
        return ResponseEntity.ok(consumer);
    }

    @PostMapping("/{id}/inactive")
    public ResponseEntity<Boolean> markInactive(@PathVariable String id) {
        return ResponseEntity.ok(consumerConfigService.markInactive(id));
    }

    @PostMapping("/{id}/active")
    public ResponseEntity<Boolean> markActive(@PathVariable String id) {
        return ResponseEntity.ok(consumerConfigService.markActive(id));
    }
}
