package org.reactivetoolbox.io.raw;

public record RawProperty(int offset, int size) {
    public static RawProperty raw(final int offset, final int size) {
        return new RawProperty(offset, size);
    }
}
