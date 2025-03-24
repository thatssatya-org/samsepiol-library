package com.samsepiol.library.temporal.config;


import com.samsepiol.library.temporal.client.TemporalClient;
import com.samsepiol.library.temporal.worker.Worker;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class WorkerConfig {
    private static WorkerFactory factory = null;

    @Bean
    public Map<Class<? extends TemporalWorkflow>, Worker> workerMap(TemporalClient temporalClient, List<Worker> workers) {
        factory(temporalClient, workers);
        return workers.stream()
                .flatMap(worker -> worker.getWorkflowImplementationTypes().stream().map(type -> Map.entry(type, worker)))
                .collect(Collectors.toMap(
                        workerEntry -> (Class<? extends TemporalWorkflow>)
                                Arrays.stream(workerEntry.getKey().getInterfaces()).findFirst().orElseThrow(),
                        Map.Entry::getValue));
    }

    public synchronized void startFactory() {
        if (!factory.isStarted()) {
            factory.start();
            log.info("Temporal Worker Factory started!");
        }
    }

    private synchronized void factory(TemporalClient temporalClient, List<Worker> workers) {
        if (Objects.isNull(factory)) {
            factory = temporalClient.getNewWorkerFactory();
        }
        addWorkersToFactory(workers);
    }


    public void addWorkersToFactory(List<Worker> workers) {
        workers.forEach(this::addWorkerToFactory);
    }

    private void addWorkerToFactory(Worker worker) {
        var newWorker = factory.newWorker(worker.getTaskQueue(), WorkerOptions.getDefaultInstance());

        worker.getWorkflowImplementationTypes().forEach(newWorker::registerWorkflowImplementationTypes);
        worker.getActivityImplementations().forEach(newWorker::registerActivitiesImplementations);

        log.info("{} added to factory", worker.getClass().getName());
    }

}
