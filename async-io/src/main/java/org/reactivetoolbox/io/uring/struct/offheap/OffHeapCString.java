package org.reactivetoolbox.io.uring.struct.offheap;

import org.reactivetoolbox.io.raw.RawMemory;

import java.nio.charset.StandardCharsets;

public class OffHeapCString extends AbstractOffHeapStructure<OffHeapCString> {
    private OffHeapCString(final byte[] input) {
        super(input.length + 1);
        clear();
        RawMemory.putByteArray(address(), input);
    }

    public static OffHeapCString cstring(final String string) {
        return new OffHeapCString(string.getBytes(StandardCharsets.UTF_8));
    }
}
