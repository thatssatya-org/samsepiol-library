package com.samsepiol.library.http.client.impl;

import com.samsepiol.library.core.exception.LibraryException;
import com.samsepiol.library.core.exception.SerializationException;
import com.samsepiol.library.http.client.HttpClient;
import com.samsepiol.library.http.config.HttpConfig;
import com.samsepiol.library.http.constants.AsyncExecutorPool;
import com.samsepiol.library.http.exception.HttpClientException;
import com.samsepiol.library.http.request.ApiRequest;
import com.samsepiol.library.http.response.HttpResponseStatus;
import com.samsepiol.library.http.response.HttpResponseEnvelope;
import com.samsepiol.library.core.util.SerializationUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Slf4j
public class DefaultHttpClient implements HttpClient, Closeable {
    private static final int MAX_DIAGNOSTIC_BODY_CHARS = 1_024;
    private static final Set<String> SENSITIVE_HEADERS = Set.of("authorization", "cookie", "set-cookie",
            "proxy-authorization", "x-api-key", "x-auth-token");
    private static final Pattern SECRET_JSON_PROPERTY = Pattern.compile(
            "(?i)(\\\"(?:password|passwd|secret|token|api[_-]?key|access[_-]?token|refresh[_-]?token|client[_-]?secret|credential)\\\"\\s*:\\s*)\\\"(?:\\\\.|[^\\\"])*\\\"");
    private final HttpConfig httpConfig;
    private final CloseableHttpAsyncClient asyncClient;

    public DefaultHttpClient(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
        this.asyncClient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(httpConfig.getConnectTimeoutMs())
                        .setConnectionRequestTimeout(httpConfig.getConnectionRequestTimeoutMs())
                        .setSocketTimeout(httpConfig.getSocketTimeoutMs())
                        .build())
                .build();
        asyncClient.start();
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

    @Override
    public @NonNull HttpResponseStatus execute(ApiRequest request) throws LibraryException {
        return executeAsyncInternal(request, httpResponse -> HttpResponseStatus.builder()
                .statusCode(httpResponse.getStatusCode())
                .build()).join();
    }

    @Override
    public <R> @NonNull HttpResponseEnvelope<R> executeWithResponse(ApiRequest request, Class<R> responseCls)
            throws LibraryException {
        return executeAsyncInternal(request, response -> HttpResponseEnvelope.<R>builder()
                .statusCode(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.isSuccessful() && !response.getBody().isBlank()
                        ? deserializedResponse(responseCls, response.getBody())
                        : null)
                .build()).join();
    }

    private <R> CompletableFuture<R> executeAsyncInternal(ApiRequest request, Class<R> responseCls) throws SerializationException {
        return executeAsyncInternal(request, responseMapper(responseCls));
    }

    private <R> CompletableFuture<R> executeAsyncInternal(ApiRequest request,
                                                          ResponseMapper<R> responseMapper) throws SerializationException {
        var futureResponse = new CompletableFuture<R>();
        var httpRequest = buildHttpUriRequest(request);
        var apiConfig = apiConfig(request);
        logRequestDiagnostics(apiConfig, httpRequest, request);
        asyncClient.execute(httpRequest, getFutureCallback(responseMapper, futureResponse, httpRequest, apiConfig));
        return futureResponse;
    }

    private HttpUriRequest buildHttpUriRequest(ApiRequest request) throws SerializationException {
        var httpRequest =   getHttpRequestBase(request);
        configureTimeouts(request, httpRequest);
        request.getHeaders().forEach(httpRequest::setHeader);
        return httpRequest;
    }

    private void configureTimeouts(ApiRequest request, HttpRequestBase httpRequest) {
        var apiConfig = apiConfig(request);
        httpRequest.setConfig(RequestConfig.custom()
                .setConnectTimeout(apiConfig.getConnectionTimeoutMs() == null
                        ? httpConfig.getConnectTimeoutMs()
                        : apiConfig.getConnectionTimeoutMs())
                .setConnectionRequestTimeout(httpConfig.getConnectionRequestTimeoutMs())
                .setSocketTimeout(apiConfig.getReadTimeoutMs() == null
                        ? httpConfig.getSocketTimeoutMs()
                        : apiConfig.getReadTimeoutMs())
                .build());
    }

    private HttpRequestBase getHttpRequestBase(ApiRequest request) throws SerializationException {
        if (isPostApi(request)) {
            var httpPost = new HttpPost(URI.create(buildUrl(request)));
            httpPost.setEntity(new StringEntity(serializedRequestBody(request.getBody()), ContentType.APPLICATION_JSON));
            return httpPost;
        } else if (isGetApi(request)) {
            return new HttpGet(URI.create(buildUrl(request)));

        }
        throw HttpClientException.builder().build();
    }

    private static <R> FutureCallback<HttpResponse> getFutureCallback(ResponseMapper<R> responseMapper,
                                                                      CompletableFuture<R> futureResponse,
                                                                      HttpUriRequest httpRequest,
                                                                      HttpConfig.ServiceConfig.ApiConfig apiConfig) {
        return new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                try {
                    var response = toResponseEnvelope(httpResponse, apiConfig);
                    logResponseDiagnostics(apiConfig, httpRequest, response);
                    futureResponse.complete(responseMapper.map(response));
                } catch (IOException | RuntimeException exception) {
                    futureResponse.completeExceptionally(HttpClientException.builder().build());
                }
            }

            @Override
            public void failed(Exception exception) {
                log.error("Http call failed for: {}", httpRequest.getURI(), exception);
                futureResponse.completeExceptionally(HttpClientException.builder().build());
            }

            @Override
            public void cancelled() {
                log.error("Http call cancelled for: {}", httpRequest.getURI());
                futureResponse.completeExceptionally(HttpClientException.builder().build());
            }
        };
    }

    private static <R> ResponseMapper<R> responseMapper(Class<R> responseCls) {
        return response -> {
            if (response.isSuccessful() && !response.getBody().isBlank()) {
                return deserializedResponse(responseCls, response.getBody());
            }
            throw HttpClientException.builder().build();
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
        return httpConfig.getServiceConfig(request.getService()).isSecured() ? "https://" : "http://";
    }

    private static String serializedRequestBody(Object object) throws SerializationException {
        return SerializationUtil.convertToString(object);
    }

    private HttpConfig.ServiceConfig.ApiConfig apiConfig(ApiRequest request) {
        return httpConfig.getServiceConfig(request.getService()).getApiConfig(request.getApi());
    }

    private static HttpResponseEnvelope<String> toResponseEnvelope(HttpResponse response,
                                                                    HttpConfig.ServiceConfig.ApiConfig apiConfig) throws IOException {
        var headers = new LinkedHashMap<String, List<String>>();
        for (Header header : response.getAllHeaders()) {
            headers.computeIfAbsent(header.getName(), ignored -> new ArrayList<>()).add(header.getValue());
        }
        var body = response.getEntity() == null ? "" : readBoundedBody(response.getEntity(),
                apiConfig.getMaxResponseBodyBytes());
        return HttpResponseEnvelope.<String>builder()
                .statusCode(response.getStatusLine().getStatusCode())
                .headers(headers)
                .body(body)
                .build();
    }

    private static String readBoundedBody(HttpEntity entity, Integer configuredMaxBytes) throws IOException {
        var maxBytes = configuredMaxBytes == null ? 262_144 : configuredMaxBytes;
        if (maxBytes <= 0) {
            throw HttpClientException.builder().build();
        }
        if (entity.getContentLength() > maxBytes) {
            throw HttpClientException.builder().build();
        }
        if (entity.getContentLength() >= 0) {
            return new String(EntityUtils.toByteArray(entity), StandardCharsets.UTF_8);
        }
        return readChunkedBody(entity.getContent(), maxBytes);
    }

    private static String readChunkedBody(InputStream stream, int maxBytes) throws IOException {
        var buffer = new java.io.ByteArrayOutputStream(Math.min(maxBytes, 8_192));
        var chunk = new byte[Math.min(maxBytes, 8_192)];
        try (stream) {
            int read;
            while ((read = stream.read(chunk)) != -1) {
                if (buffer.size() + read > maxBytes) {
                    throw new IOException("HTTP response body exceeds configured maximum");
                }
                buffer.write(chunk, 0, read);
            }
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static void logRequestDiagnostics(HttpConfig.ServiceConfig.ApiConfig apiConfig, HttpUriRequest request,
                                              ApiRequest apiRequest) throws SerializationException {
        if (apiConfig.isRequestLoggingEnabled()) {
            var body = apiRequest.getBody() == null ? "" : redactAndTruncate(serializedRequestBody(apiRequest.getBody()));
            log.info("HTTP request method={} headers={} body={}", request.getMethod(), safeHeaders(request.getAllHeaders()), body);
        }
    }

    private static void logResponseDiagnostics(HttpConfig.ServiceConfig.ApiConfig apiConfig, HttpUriRequest request,
                                               HttpResponseEnvelope<String> response) {
        if (apiConfig.isResponseLoggingEnabled()) {
            log.info("HTTP response method={} status={} headers={} body={}", request.getMethod(),
                    response.getStatusCode(), safeHeaders(response.getHeaders()),
                    redactAndTruncate(response.getBody()));
        }
    }

    private static Map<String, List<String>> safeHeaders(Header[] headers) {
        var result = new LinkedHashMap<String, List<String>>();
        for (Header header : headers) {
            if (!SENSITIVE_HEADERS.contains(header.getName().toLowerCase(Locale.ROOT))) {
                result.computeIfAbsent(header.getName(), ignored -> new ArrayList<>()).add(header.getValue());
            }
        }
        return result;
    }

    private static Map<String, List<String>> safeHeaders(Map<String, List<String>> headers) {
        return headers.entrySet().stream()
                .filter(entry -> !SENSITIVE_HEADERS.contains(entry.getKey().toLowerCase(Locale.ROOT)))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (left, right) -> left, LinkedHashMap::new));
    }

    private static String redactAndTruncate(String body) {
        var redacted = SECRET_JSON_PROPERTY.matcher(body).replaceAll("$1\"[REDACTED]\"");
        return redacted.length() <= MAX_DIAGNOSTIC_BODY_CHARS ? redacted
                : redacted.substring(0, MAX_DIAGNOSTIC_BODY_CHARS) + "…[truncated]";
    }

    @Override
    public void close() throws IOException {
        asyncClient.close();
    }

    @FunctionalInterface
    private interface ResponseMapper<R> {
        R map(HttpResponseEnvelope<String> httpResponse) throws IOException, SerializationException;
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
