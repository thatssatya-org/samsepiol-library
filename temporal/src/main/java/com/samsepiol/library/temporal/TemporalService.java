package com.samsepiol.library.temporal;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import lombok.NonNull;

/**
 * Exposes temporal functionalities for common usage across application
 * @author satyajitroy
 */
public interface TemporalService {

    WorkflowExecutionStatus getWorkflowStatus(@NonNull String workflowId);

    boolean isHealthy();
}
