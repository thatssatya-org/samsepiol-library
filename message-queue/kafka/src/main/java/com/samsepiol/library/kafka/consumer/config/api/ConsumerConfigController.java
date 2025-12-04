package com.samsepiol.library.kafka.consumer.config.api;

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

@RestController
@RequestMapping("/api/consumers/kafka")
@RequiredArgsConstructor
@ConditionalOnProperty("spring.kafka.bootstrap-servers")
public class ConsumerConfigController {
    private final ConsumerConfigService consumerConfigService;

    @GetMapping("/active")
    public ResponseEntity<List<ConsumerConfigEntity>> fetchActive() {
        return ResponseEntity.ok(consumerConfigService.findActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<ConsumerConfigEntity>> fetchInactive() {
        return ResponseEntity.ok(consumerConfigService.findInactive());
    }

    @PostMapping
    public ResponseEntity<Void> start(@RequestBody ConsumerConfigEntity consumerConfigEntity) {
        consumerConfigService.insert(consumerConfigEntity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/inactive")
    public ResponseEntity<Void> markInactive(@PathVariable String id) {
        consumerConfigService.markInactive(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/active")
    public ResponseEntity<Void> markActive(@PathVariable String id) {
        consumerConfigService.markActive(id);
        return ResponseEntity.ok().build();
    }
}
