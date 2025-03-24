package com.samsepiol.library.http.config;

import com.samsepiol.library.http.constants.AsyncExecutorPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean(AsyncExecutorPool.VIRTUAL_THREAD_PER_TASK_EXECUTOR)
    public ExecutorService virtualThreadPoolTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
