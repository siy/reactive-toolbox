package org.reactivetoolbox.io.async.util;

import org.reactivetoolbox.io.raw.RawMemory;
import org.reactivetoolbox.io.uring.struct.offheap.AbstractOffHeapStructure;

public class OffHeapBuffer extends AbstractOffHeapStructure<OffHeapBuffer> {
    private int used;

    private OffHeapBuffer(final byte[] input) {
        super(input.length);
        RawMemory.putByteArray(address(), input);
        used = input.length;
    }

    private OffHeapBuffer(final int size) {
        super(size);
        used = 0;
    }

    public static OffHeapBuffer fromBytes(final byte[] input) {
        return new OffHeapBuffer(input);
    }

    public static OffHeapBuffer fixedSize(final int size) {
        return new OffHeapBuffer(size);
    }

    public int used() {
        return used;
    }

    public OffHeapBuffer used(final int used) {
        this.used = Math.min(size(), used);
        return this;
    }

    public byte[] export() {
        return RawMemory.getByteArray(address(), used);
    }
}
