package com.samsepiol.library.http.response;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseEnvelopeTest {
    @Test
    void normalizesHeaderNamesForConditionalResponseConsumers() {
        var response = new HttpResponseEnvelope(304, Map.of("ETag", List.of("\"revision\"")), "");

        assertThat(response.firstHeader("etag")).contains("\"revision\"");
        assertThat(response.firstHeader("ETAG")).contains("\"revision\"");
        assertThat(response.isSuccessful()).isFalse();
    }
}
