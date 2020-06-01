package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawMemory;

import java.nio.charset.StandardCharsets;

public class RawCString extends AbstractDisposableStructure<RawCString> {
    private RawCString(final byte[] input) {
        super(input.length + 1);
        clear();
        RawMemory.putByteArray(address(), input);
    }

    public static RawCString rawCString(final String string) {
        return new RawCString(string.getBytes(StandardCharsets.UTF_8));
    }
}
