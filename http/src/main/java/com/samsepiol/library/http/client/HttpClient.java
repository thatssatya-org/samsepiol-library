package com.samsepiol.library.http.client;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.http.request.ApiRequest;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {

    <T> T execute(ApiRequest request, Class<T> responseCls) throws LibraryException;

    <T> CompletableFuture<T> executeAsync(ApiRequest request, Class<T> responseCls) throws LibraryException;
}
