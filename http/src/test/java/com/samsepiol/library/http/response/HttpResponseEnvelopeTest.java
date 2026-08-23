package com.samsepiol.library.http.response;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseEnvelopeTest {
    @Test
    void normalizesHeaderNamesForConditionalResponseConsumers() {
        var response = HttpResponseEnvelope.<String>builder()
                .statusCode(304)
                .headers(Map.of("ETag", List.of("\"revision\"")))
                .body("")
                .build();

        assertThat(response.firstHeader("etag")).contains("\"revision\"");
        assertThat(response.firstHeader("ETAG")).contains("\"revision\"");
        assertThat(response.isSuccessful()).isFalse();
    }

    @Test
    void retainsTheRequestedDeserializedBodyType() {
        var response = HttpResponseEnvelope.<Integer>builder()
                .statusCode(200)
                .headers(Map.of())
                .body(7)
                .build();

        assertThat(response.getBody()).isEqualTo(7);
    }
}
