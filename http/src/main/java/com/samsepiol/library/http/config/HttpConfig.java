package com.samsepiol.library.http.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "http-config")
@Data
public class HttpConfig {
    private int connectTimeoutMs = 5_000;
    private int connectionRequestTimeoutMs = 5_000;
    private int socketTimeoutMs = 8_000;
    private Map<String, ServiceConfig> serviceConfigs = new HashMap<>();
    private List<HttpConfigService> httpConfigServices;

    @Autowired
    public HttpConfig(List<HttpConfigService> httpConfigServices) {
        this.httpConfigServices = httpConfigServices;
    }

    @PostConstruct
    public void init() {
        serviceConfigs.putAll(httpConfigServices.stream()
                .flatMap(httpConfigService -> httpConfigService.getConfig().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public ServiceConfig getServiceConfig(String service) {
        return serviceConfigs.get(service);
    }

    @Data
    public static class ServiceConfig {
        private String baseUrl;
        private Boolean secured;
        private Map<String, ApiConfig> apiConfigs;

        public boolean isSecured() {
            return secured;
        }

        public ApiConfig getApiConfig(String api) {
            return apiConfigs.get(api);
        }

        @Data
        public static class ApiConfig {
            private String path;
            private HttpMethod method;

            public boolean isGET() {
                return HttpMethod.GET.equals(method);
            }

            public boolean isPOST() {
                return HttpMethod.POST.equals(method);
            }

            public boolean isPATCH() {
                return HttpMethod.PATCH.equals(method);
            }

            public boolean isDELETE() {
                return HttpMethod.DELETE.equals(method);
            }

        }
    }
}
