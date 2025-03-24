package com.samsepiol.library.core.exception;

import com.samsepiol.library.core.exception.enums.Error;

public class SerializationException extends LibraryException {

    protected SerializationException() {
        super(Error.SERIALIZATION_ERROR);
    }

    public static SerializationException build() {
        return new SerializationException();
    }
}
