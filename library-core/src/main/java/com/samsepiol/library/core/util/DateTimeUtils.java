package com.samsepiol.library.core.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@UtilityClass
public class DateTimeUtils {
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Kolkata");

    public static Long currentEpochMillis() {
        return LocalDateTime.now(ZONE_ID).toEpochSecond(ZoneOffset.ofHours(0)) * 1_000;
    }
}
