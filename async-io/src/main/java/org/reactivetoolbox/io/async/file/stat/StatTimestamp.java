package org.reactivetoolbox.io.async.file.stat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.StringJoiner;

//TODO: toString
public class StatTimestamp {
    private final long seconds;
    private final int nanos;

    private StatTimestamp(final long seconds, final int nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
    }

    public static StatTimestamp timestamp(final long seconds, final int nanos) {
        return new StatTimestamp(seconds, nanos);
    }

    public long seconds() {
        return seconds;
    }

    public int nanos() {
        return nanos;
    }

    public LocalDateTime localDateTime() {
        return LocalDateTime.ofEpochSecond(seconds, nanos, ZoneOffset.UTC);
    }

    @Override
    public String toString() {
        return localDateTime().toString();
    }
}
