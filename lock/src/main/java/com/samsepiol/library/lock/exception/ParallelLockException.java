package com.samsepiol.library.lock.exception;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.core.exception.enums.Error;

public class ParallelLockException extends LibraryException {

    public ParallelLockException() {
        super(Error.PARALLEL_LOCK);
    }

    public static ParallelLockException create() {
        return new ParallelLockException();
    }
}
