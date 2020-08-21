package org.reactivetoolbox.io.async.common;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SizeT sizeT) {
            return value == sizeT.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return "SizeT(" + value + ")";
    }
}
