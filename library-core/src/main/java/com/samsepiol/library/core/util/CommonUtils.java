package com.samsepiol.library.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {
    public static boolean paramsNotEmpty(Object... objects) {
        return Arrays.stream(objects).noneMatch(ObjectUtils::isEmpty);
    }
}
