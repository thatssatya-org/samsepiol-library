package com.samsepiol.library.core.async.impl;

import com.samsepiol.library.core.async.AsyncExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Slf4j
public class AbstractAsyncExecutor implements AsyncExecutor {
    private final ExecutorService executorService;

    @Override
    public <T> CompletableFuture<T> supply(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executorService);
    }

    @Override
    public <T> void run(Supplier<T> supplier) {
        CompletableFuture.runAsync(supplier::get);
    }
}
