package org.reactivetoolbox.core.lang.support;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class KSUIDTest {
    @Test
    void canBeCreated() {
        final var now = ZonedDateTime.now(ZoneOffset.UTC)
                                     .minus(1, ChronoUnit.SECONDS);
        final var ksuid = KSUID.create();

        assertEquals(27, ksuid.encoded().length());
        assertTrue(ksuid.dateTime().compareTo(now) >= 0);
        assertTrue(ksuid.dateTime().isBefore(ZonedDateTime.now(ZoneOffset.UTC)));
    }

    @Test
    void canBeRestoredFromString() {
        final var originalDateTime = ZonedDateTime.of(2017, 10, 10, 5, 1, 40, 0, ZoneId.of("UTC"));
        final var ksuid = KSUID.fromString("0uk1Hbc9dQ9pxyTqJ93IUrfhdGq");
        final var payload = new byte[]{(byte) 0x98, (byte) 0x50, (byte) 0xEE, (byte) 0xEC, (byte) 0x19, (byte) 0x1B, (byte) 0xF4, (byte) 0xFF,
                                       (byte) 0x26, (byte) 0xF9, (byte) 0x93, (byte) 0x15, (byte) 0xCE, (byte) 0x43, (byte) 0xB0, (byte) 0xC8};
        ksuid.onSuccess(id -> assertEquals(107611700, id.timestamp()))
             .onSuccess(id -> assertArrayEquals(payload, id.payload()))
             .onSuccess(id -> assertEquals(originalDateTime, id.dateTime()))
             .onFailure(failure -> fail());
    }
}