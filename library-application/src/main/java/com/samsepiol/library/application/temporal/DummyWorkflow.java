package com.samsepiol.library.application.temporal;

import com.samsepiol.library.core.util.IdentityUtils;
import com.samsepiol.library.temporal.constants.Queues;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DummyWorkflow extends TemporalWorkflow {

    @WorkflowMethod
    void process();

    static DummyWorkflow getInstance(WorkflowClient workflowClient) {
        return workflowClient.newWorkflowStub(DummyWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(IdentityUtils.generateId("DW"))
                        .setTaskQueue(Queues.WORKFLOWS)
                .build());
    }
}
