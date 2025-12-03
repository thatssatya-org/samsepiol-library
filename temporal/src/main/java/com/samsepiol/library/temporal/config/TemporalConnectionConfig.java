package com.samsepiol.library.temporal.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.temporal.client.WorkflowClient;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return WorkflowClient.newInstance(serviceStubs);
    }

    @Bean
    public ScheduleClient scheduleClient(WorkflowServiceStubs serviceStubs) {
        return ScheduleClient.newInstance(serviceStubs);
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
                .build();
    }
}
