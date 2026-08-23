package com.samsepiol.library.http.client;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.http.request.ApiRequest;
import com.samsepiol.library.http.response.HttpResponseStatus;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {

    <T> T execute(ApiRequest request, Class<T> responseCls) throws LibraryException;

    @NonNull HttpResponseStatus execute(ApiRequest request) throws LibraryException;

    <T> CompletableFuture<T> executeAsync(ApiRequest request, Class<T> responseCls) throws LibraryException;
}
