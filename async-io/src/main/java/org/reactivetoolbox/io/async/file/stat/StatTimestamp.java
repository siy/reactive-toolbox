package org.reactivetoolbox.io.async.file.stat;

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
}
