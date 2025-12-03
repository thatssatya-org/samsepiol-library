package com.samsepiol.library.temporal;

import com.samsepiol.library.temporal.config.WorkerConfig;
import com.samsepiol.library.temporal.worker.Worker;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemporalWorkflowFactory {
    private final Map<Class<? extends TemporalWorkflow>, Worker> workerMap;
    private final WorkerConfig workerConfig;

    @PostConstruct
    public void init() {
        workerConfig.startFactory();
    }

    public <T extends TemporalWorkflow> T newWorkflow(Class<T> workflowCls) {
        return workerMap.get(workflowCls).newWorkflow(workflowCls);
    }
}
