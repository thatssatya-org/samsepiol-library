package com.samsepiol.library.core.util;

import lombok.experimental.UtilityClass;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@UtilityClass
public class DateTimeUtils {
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Kolkata");
    private static final Clock DEFAULT_CLOCK = Clock.system(DEFAULT_ZONE_ID);

    public static Long currentEpochMillis() {
        return DEFAULT_CLOCK.millis();
    }

    public static ZoneId defaultZoneId() {
        return DEFAULT_ZONE_ID;
    }

    public static LocalDate toDefaultZoneDate(Instant instant) {
        return instant.atZone(DEFAULT_ZONE_ID).toLocalDate();
    }
}
