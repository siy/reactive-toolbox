package org.reactivetoolbox.io.async.common;

/**
 * Representation of the various values meaning 'offset' inside something, for example offset from beginning of the file.
 */
public class OffsetT {
    private final long value;

    private OffsetT(final long value) {
        this.value = value;
    }

    public static final OffsetT ZERO = offsetT(0L);

    public static OffsetT offsetT(final long value) {
        return new OffsetT(value);
    }

    public long value() {
        return value;
    }

    @Override
    public String toString() {
        return "OffsetT(" + value + ")";
    }
}
