package com.samsepiol.library.http.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "http-config")
@Data
public class HttpConfig {
    private Boolean enableAsync;
    private Map<String, ServiceConfig> serviceConfigs;

    @Autowired
    public HttpConfig(List<HttpConfigService> httpConfigServices) {
        serviceConfigs = httpConfigServices.stream()
                .flatMap(httpConfigService -> httpConfigService.getConfig().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean asyncEnabled() {
        return Objects.requireNonNullElse(enableAsync, Boolean.FALSE);
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
