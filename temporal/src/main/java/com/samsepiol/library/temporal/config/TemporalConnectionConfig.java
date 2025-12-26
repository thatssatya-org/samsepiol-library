package com.samsepiol.library.temporal.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.client.schedules.ScheduleClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "temporal-config")
@ConditionalOnProperty("temporal-config.host")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TemporalConnectionConfig {
    private String host;
    private Integer port;
    private String namespace;

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(serviceStubs, WorkflowClientOptions.newBuilder()
                .setNamespace(namespace)
                .build());
    }

    @Bean
    public ScheduleClient scheduleClient(WorkflowServiceStubs serviceStubs) {
        return ScheduleClient.newInstance(serviceStubs, ScheduleClientOptions.newBuilder()
                .setNamespace(namespace)
                .build());
    }

    @Bean
    public WorkflowServiceStubs buildWorkflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(buildWorkflowServiceStubsOptions());
    }

    private String getTarget() {
        return String.format("%s:%d", host, port);
    }

    private WorkflowServiceStubsOptions buildWorkflowServiceStubsOptions() {
        return WorkflowServiceStubsOptions.newBuilder()
                .setTarget(getTarget())
                .setEnableKeepAlive(Boolean.TRUE)
                .setKeepAliveTime(Duration.ofSeconds(30))
                .setKeepAliveTimeout(Duration.ofSeconds(10))
                .setChannelInitializer(channelBuilder -> {
                    if (channelBuilder != null) {
                        channelBuilder
                                .keepAliveTime(30, TimeUnit.SECONDS)
                                .keepAliveTimeout(10, TimeUnit.SECONDS);
                        // Note: Netty's proxy connect timeout is often separate,
                        // but ensuring the gRPC channel is patient helps.
                    }
                })
                .build();
    }
}
