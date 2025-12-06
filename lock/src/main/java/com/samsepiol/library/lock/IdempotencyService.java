package com.samsepiol.library.lock;


import com.samsepiol.library.lock.exception.ParallelLockException;

import java.util.function.Supplier;

public interface IdempotencyService {

    <T> T execute(String idempotencyKey, Supplier<T> supplier) throws ParallelLockException;

}
