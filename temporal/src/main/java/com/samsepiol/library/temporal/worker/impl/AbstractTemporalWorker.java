package com.samsepiol.library.temporal.worker.impl;

import com.samsepiol.library.temporal.activity.TemporalActivity;
import com.samsepiol.library.temporal.client.TemporalClient;
import com.samsepiol.library.temporal.worker.Worker;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTemporalWorker implements Worker {
    private final Set<Class<? extends TemporalWorkflow>> workflowImplClasses;
    private final String taskQueue;
    private final Set<? extends TemporalActivity> activityImplementations;
    private final Map<Class<? extends TemporalWorkflow>, WorkflowOptions> workflowOptionsMap;
    protected final TemporalClient temporalClient;

    protected AbstractTemporalWorker(Class<? extends TemporalWorkflow> workFlowImplClass,
                                     String taskQueue,
                                     TemporalActivity activityImplClass,
                                     WorkflowOptions workflowOptions,
                                     TemporalClient temporalClient) {
        this(Set.of(workFlowImplClass),
                taskQueue,
                Set.of(activityImplClass),
                Map.of((Class<? extends TemporalWorkflow>) (workFlowImplClass.getGenericInterfaces()[0]), workflowOptions),
                temporalClient);
    }

    @Override
    public String getTaskQueue() {
        return taskQueue;
    }

    @Override
    public Set<Class<? extends TemporalWorkflow>> getWorkflowImplementationTypes() {
        return workflowImplClasses;
    }

    @Override
    public Set<? extends TemporalActivity> getActivityImplementations() {
        return activityImplementations;
    }

    @Override
    public <T extends TemporalWorkflow> T newWorkflow(Class<T> cls) {
        return temporalClient.newWorkflow(cls, WorkflowOptions.newBuilder()
                .setTaskQueue(taskQueue)
                .setWorkflowId(Objects.requireNonNullElse(workflowOptionsMap.get(cls).getWorkflowId(), UUID.randomUUID().toString()))
                .setRetryOptions(RetryOptions.getDefaultInstance())
                .build());
    }

}
