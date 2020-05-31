package org.reactivetoolbox.io.api;

/**
 * Representation of the various values meaning 'size' of the something, for example size of read/writen chunk of data.
 */
public class SizeT {
    private final long value;

    private SizeT(final long value) {
        this.value = value;
    }

    public static final SizeT ZERO = sizeT(0L);

    public static SizeT sizeT(final long value) {
        return new SizeT(value);
    }

    public long value() {
        return value;
    }
}
