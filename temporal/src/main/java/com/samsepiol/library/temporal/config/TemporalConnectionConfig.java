package com.samsepiol.library.temporal.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "temporal-config")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TemporalConnectionConfig {
    private String host;
    private Integer port;

    @Bean
    public WorkflowClient workflowClient() {
        return WorkflowClient.newInstance(buildWorkflowServiceStubs());
    }

    private WorkflowServiceStubsOptions buildWorkflowServiceStubsOptions() {
        return WorkflowServiceStubsOptions.newBuilder()
                .setTarget(getTarget())
                .build();
    }

    private WorkflowServiceStubs buildWorkflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(buildWorkflowServiceStubsOptions());
    }

    private String getTarget() {
        return String.format("%s:%d", host, port);
    }
}
