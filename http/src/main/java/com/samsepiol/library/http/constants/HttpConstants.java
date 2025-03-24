package com.samsepiol.library.http.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHeaders;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Headers {
        public static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;
        public static final String CONTENT_TYPE = HttpHeaders.CONTENT_TYPE;
        public static final String ORIGIN = "Origin";
    }
}
