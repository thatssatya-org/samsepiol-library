package com.samsepiol.library.http.client.impl;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.core.exception.SerializationException;
import com.samsepiol.library.http.client.HttpClient;
import com.samsepiol.library.http.config.HttpConfig;
import com.samsepiol.library.http.constants.AsyncExecutorPool;
import com.samsepiol.library.http.request.ApiRequest;
import com.samsepiol.library.core.util.SerializationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class DefaultHttpClient implements HttpClient, Closeable {
    private static final CloseableHttpAsyncClient ASYNC_CLIENT = HttpAsyncClients.createDefault();
    private final HttpConfig httpConfig;

    public DefaultHttpClient(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
        ASYNC_CLIENT.start();
    }

    @Override
    @Async(AsyncExecutorPool.VIRTUAL_THREAD_PER_TASK_EXECUTOR)
    public <R> CompletableFuture<R> executeAsync(ApiRequest request, Class<R> responseCls) throws LibraryException {
        return executeAsyncInternal(request, responseCls);
    }

    @Override
    public <R> R execute(ApiRequest request, Class<R> responseCls) throws LibraryException {
        return executeAsyncInternal(request, responseCls).join();
    }

    private <R> CompletableFuture<R> executeAsyncInternal(ApiRequest request, Class<R> responseCls) throws SerializationException {
        var futureResponse = new CompletableFuture<R>();
        var httpRequest = buildHttpUriRequest(request);
        log.info("Executing {} request to : {}", httpRequest.getMethod(), httpRequest.getURI());
        ASYNC_CLIENT.execute(httpRequest, getFutureCallback(responseCls, futureResponse, httpRequest));
        return futureResponse;
    }

    private HttpUriRequest buildHttpUriRequest(ApiRequest request) throws SerializationException {
        var httpRequest =   getHttpRequestBase(request);
        request.getHeaders().forEach(httpRequest::setHeader);
        return httpRequest;
    }

    private HttpRequestBase getHttpRequestBase(ApiRequest request) throws SerializationException {
        if (isPostApi(request)) {
            var httpPost = new HttpPost(URI.create(buildUrl(request)));
            httpPost.setEntity(new StringEntity(serializedRequestBody(request.getBody()), ContentType.APPLICATION_JSON));
            return httpPost;
        } else if (isGetApi(request)) {
            return new HttpGet(URI.create(buildUrl(request)));

        }
        throw new UnsupportedOperationException("Request method not supported");
    }

    private static <R> FutureCallback<HttpResponse> getFutureCallback(Class<R> responseCls,
                                                                      CompletableFuture<R> futureResponse,
                                                                      HttpUriRequest httpRequest) {
        return new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                try {
                    log.info("Response received with status: {}", httpResponse.getStatusLine().getStatusCode());
                    var responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                    log.info("Body: {}", responseBody);
                    if (Set.of(200, 403).contains(httpResponse.getStatusLine().getStatusCode()) && !responseBody.isBlank()) {
                        futureResponse.complete(deserializedResponse(responseCls, responseBody));
                    } else {
                        futureResponse.completeExceptionally(new RuntimeException("Exception occurred!"));
                    }
                } catch (IOException | SerializationException exception) {
                    futureResponse.completeExceptionally(exception);
                }
            }

            @Override
            public void failed(Exception exception) {
                log.error("Http call failed for: {}", httpRequest.getURI(), exception);
                futureResponse.completeExceptionally(exception);
            }

            @Override
            public void cancelled() {
                log.error("Http call cancelled for: {}", httpRequest.getURI());
                futureResponse.completeExceptionally(new RuntimeException("Http operation cancelled"));
            }
        };
    }

    private static <T> T deserializedResponse(Class<T> responseCls, Object response) throws SerializationException {
        return SerializationUtil.convertToEntity(response, responseCls);
    }

    private boolean isDeleteApi(ApiRequest request) {
        return httpConfig.getServiceConfig(request.getService()).getApiConfig(request.getApi()).isDELETE();
    }

    private boolean isPatchApi(ApiRequest request) {
        return httpConfig.getServiceConfig(request.getService()).getApiConfig(request.getApi()).isPATCH();
    }

    private boolean isGetApi(ApiRequest request) {
        return httpConfig.getServiceConfig(request.getService()).getApiConfig(request.getApi()).isGET();
    }

    private boolean isPostApi(ApiRequest request) {
        return httpConfig.getServiceConfig(request.getService()).getApiConfig(request.getApi()).isPOST();
    }

    private String buildUrl(ApiRequest request) {
        var protocol = buildProtocol(request);
        var baseUrl = httpConfig.getServiceConfig(request.getService()).getBaseUrl();
        var api = httpConfig.getServiceConfig(request.getService()).getApiConfig(request.getApi()).getPath();
        return String.format("%s%s%s", protocol, baseUrl, api);
    }

    private String buildProtocol(ApiRequest request) {
        return httpConfig.getServiceConfig(request.getService()).isSecured() ? "https://" : "http:";
    }

    private static String serializedRequestBody(Object object) throws SerializationException {
        return SerializationUtil.convertToString(object);
    }

    @Override
    public void close() throws IOException {
        ASYNC_CLIENT.close();
    }

//    private HttpRequest buildHttpRequest(ApiRequest request) {
//        var builder = HttpRequest
//                .newBuilder(URI.create(buildUrl(request)))
//                .header("Content-Type", request.getContentType())
//                .header("Origin", request.getOrigin())
//                .header("Authorization", request.getAuthorization());
//
//        if (isPostApi(request)) {
//            builder = builder.POST(HttpRequest.BodyPublishers.ofString(serializedRequestBody(request.getBody())));
//        } else if (isGetApi(request)) {
//            builder = builder.GET();
//        } else if (isPatchApi(request)) {
//            builder = builder.PUT(HttpRequest.BodyPublishers.ofString(serializedRequestBody(request.getBody())));
//        } else if (isDeleteApi(request)) {
//            builder = builder.DELETE();
//        }
//
//        return builder.build();
//    }
//
//    @Override
//    public <T> T execute(ApiRequest request, Class<T> responseCls) {
//        try {
//            log.info("Http Client Thread: {}", Thread.currentThread());
//            var httpRequest = buildHttpRequest(request);
//            log.info("Executing {} request to : {}", httpRequest.method(), httpRequest.uri());
//            var response = client.send(buildHttpRequest(request), HttpResponse.BodyHandlers.ofString());
//
//            log.info("Response received with status: {}", response.statusCode());
//            log.info("Body: {}", response.body());
//            return deserializedResponse(responseCls, response.body());
//        } catch (UserProfileException exception) {
//            throw exception;
//        } catch (Exception exception) {
//            log.error("Exception occurred while executing api request: ", exception);
//            throw InternalServerException.build();
//        }
//    }

//    @Override
//    @Async(AsyncExecutorPool.VIRTUAL_THREAD_PER_TASK_EXECUTOR)
//    public <R> HttpClientResponse<?, R> execute(ApiRequest request, Class<R> responseCls) {
//        var futureResponse = new CompletableFuture<R>();
//
//        var httpRequest = buildHttpUriRequest(request);
//        log.info("Http Client Thread: {}", Thread.currentThread());
//        log.info("Executing {} request to : {}", httpRequest.getMethod(), httpRequest.getURI());
//
//        asyncClient.execute(httpRequest, getFutureCallback(responseCls, futureResponse, httpRequest));
//
//        if (httpConfig.asyncEnabled()) {
//            return AsyncHttpClientResponse.<R>builder()
//                    .response(futureResponse)
//                    .build();
//        }
//        return SyncHttpClientResponse.<R>builder()
//                .response(futureResponse.join())
//                .build();
//    }
}
