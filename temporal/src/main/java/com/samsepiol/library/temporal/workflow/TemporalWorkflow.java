package com.samsepiol.library.temporal.workflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.workflow.Functions;

import java.util.logging.Logger;

public interface TemporalWorkflow {
    Logger LOGGER = Logger.getLogger(TemporalWorkflow.class.getName());

    static <T> boolean startGracefully(Functions.Proc1<T> workflow, T request) {
        try {
            WorkflowClient.start(workflow, request);
            return Boolean.TRUE;
        } catch (WorkflowExecutionAlreadyStarted exception) {
            LOGGER.info(String.format("Workflow execution already started for request: %s", request.toString()));
        }
        return Boolean.FALSE;
    }
}
