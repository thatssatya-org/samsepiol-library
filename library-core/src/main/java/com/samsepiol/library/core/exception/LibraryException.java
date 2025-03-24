package com.samsepiol.library.core.exception;

import com.samsepiol.library.core.exception.enums.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class LibraryException extends Exception {
    Error error;
}
