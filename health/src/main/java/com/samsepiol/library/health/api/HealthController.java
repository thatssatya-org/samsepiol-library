package com.samsepiol.library.health.api;

import com.samsepiol.library.health.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/health")
@RequiredArgsConstructor
public class HealthController {
    private final HealthCheckService healthCheckService;

    @GetMapping
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.status(getStatus()).build();
    }

    private HttpStatus getStatus() {
        return healthCheckService.isHealthy() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
    }
}
