package com.samsepiol.library.http.exception;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.core.exception.enums.Error;
import lombok.Builder;

public final class HttpClientException extends LibraryException {

    @Builder
    private HttpClientException() {
        super(Error.HTTP_CLIENT);
    }
}
