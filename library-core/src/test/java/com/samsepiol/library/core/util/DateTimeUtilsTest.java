package com.samsepiol.library.core.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeUtilsTest {
    @Test
    void usesAsiaKolkataAsTheDefaultCalendarZone() {
        assertThat(DateTimeUtils.defaultZoneId()).isEqualTo(ZoneId.of("Asia/Kolkata"));
        assertThat(DateTimeUtils.toDefaultZoneDate(Instant.parse("2026-08-23T20:00:00Z")))
                .hasToString("2026-08-24");
    }

    @Test
    void readsEpochMillisFromTheDefaultZoneClock() {
        var before = System.currentTimeMillis();

        var epochMillis = DateTimeUtils.currentEpochMillis();

        assertThat(epochMillis).isBetween(before, System.currentTimeMillis());
    }
}
