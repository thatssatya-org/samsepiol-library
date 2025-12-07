package com.samsepiol.library.temporal.config;


import com.samsepiol.library.temporal.activity.TemporalActivity;
import com.samsepiol.library.temporal.constants.Queues;
import com.samsepiol.library.temporal.workflow.TemporalWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty("temporal-config.host")
@Slf4j
public class WorkerConfig implements DisposableBean {
    private static WorkerFactory factory = null;

    private final WorkflowClient workflowClient;
    private final List<TemporalActivity> activities;

    @PostConstruct
    private synchronized void init() {
        if (Objects.isNull(factory)) {
            factory = WorkerFactory.newInstance(workflowClient);
        }
        addWorkerToFactory(activities);
        startFactory();
    }

    private void startFactory() {
        if (Objects.nonNull(factory) && !factory.isStarted()) {
            factory.start();
            log.info("Temporal Worker Factory started!");
        }
    }

    private void addWorkerToFactory(List<TemporalActivity> activities) {
        if (Objects.nonNull(factory)) {
            var newWorker = factory.newWorker(Queues.WORKFLOWS, WorkerOptions.newBuilder()
                    .setUsingVirtualThreads(true)
                    .setMaxConcurrentWorkflowTaskPollers(2)
                    .setMaxConcurrentActivityTaskPollers(2)
                    .setMaxConcurrentActivityExecutionSize(2)
                    .setMaxConcurrentWorkflowTaskExecutionSize(2)
                    .build());

            findClassesImplementing(TemporalWorkflow.class, "com.samsepiol.*").forEach(newWorker::registerWorkflowImplementationTypes);
            activities.forEach(newWorker::registerActivitiesImplementations);

            log.info("Worker added to factory");
        }
    }

    public List<? extends Class<?>> findClassesImplementing(Class<?> targetInterface, String basePackage) {
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(targetInterface));
        var components = provider.findCandidateComponents(basePackage);

        return components.stream()
                .map(component -> {
                    try {
                        return ClassUtils.forName(
                                Objects.requireNonNull(component.getBeanClassName()),
                                ClassUtils.getDefaultClassLoader()
                        );
                    } catch (ClassNotFoundException e) {
                        log.info("Exception while workflow classes scan", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

    }

    @Override
    public void destroy() {
        if (Objects.nonNull(factory) && !factory.isShutdown()) {
            factory.shutdown();
        }
    }
}
