package com.samsepiol.library.http.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpConfigTest {
    @Test
    void disablesHttpDiagnosticsByDefault() {
        var apiConfig = new HttpConfig.ServiceConfig.ApiConfig();

        assertThat(apiConfig.isRequestLoggingEnabled()).isFalse();
        assertThat(apiConfig.isResponseLoggingEnabled()).isFalse();
        assertThat(apiConfig.getMaxResponseBodyBytes()).isEqualTo(262_144);
    }
}
