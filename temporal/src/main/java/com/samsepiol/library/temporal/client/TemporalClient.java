package com.samsepiol.library.temporal.client;

import io.temporal.client.WorkflowOptions;
import io.temporal.worker.WorkerFactory;

public interface TemporalClient {
    WorkerFactory getNewWorkerFactory();

    <T> T newWorkflow(Class<T> cls, WorkflowOptions options);
}
