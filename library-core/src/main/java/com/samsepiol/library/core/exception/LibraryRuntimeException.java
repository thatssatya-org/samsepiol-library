package com.samsepiol.library.core.exception;

import com.samsepiol.library.core.exception.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class LibraryRuntimeException extends RuntimeException {
    Error error;

    private static class LibraryRuntimeExceptionWrapper extends LibraryRuntimeException {

        @Builder
        public LibraryRuntimeExceptionWrapper(Error error) {
            super(error);
        }
    }

    public static LibraryRuntimeException wrap(LibraryException exception) {
        return LibraryRuntimeExceptionWrapper.builder().error(exception.getError()).build();
    }
}
