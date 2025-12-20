package com.samsepiol.library.temporal.impl;

import com.samsepiol.library.temporal.TemporalService;
import com.samsepiol.library.temporal.config.TemporalConnectionConfig;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.api.workflowservice.v1.GetSystemInfoRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultTemporalService implements TemporalService {
    private final TemporalConnectionConfig temporalConfig;
    private final WorkflowServiceStubs workflowServiceStubs;
    private static final GetSystemInfoRequest SYSTEM_INFO_REQUEST = GetSystemInfoRequest.getDefaultInstance();

    @Override
    public WorkflowExecutionStatus getWorkflowStatus(@NonNull String workflowId) {
        WorkflowServiceGrpc.WorkflowServiceBlockingStub stub = workflowServiceStubs.blockingStub();
        DescribeWorkflowExecutionRequest request =
                DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(temporalConfig.getNamespace())
                        .setExecution(WorkflowExecution.newBuilder().setWorkflowId(workflowId))
                        .build();
        DescribeWorkflowExecutionResponse response = stub.describeWorkflowExecution(request);
        return response.getWorkflowExecutionInfo().getStatus();
    }

    @Override
    public boolean isHealthy() {
        var systemInfo = workflowServiceStubs.blockingStub().getSystemInfo(SYSTEM_INFO_REQUEST);
        return systemInfo.isInitialized();
    }
}
