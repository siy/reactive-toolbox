package org.reactivetoolbox.io.uring.struct.shape;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

public interface SocketAddressInOffsets {
    int SIZE = 16;
    RawProperty sin_family = raw(0, 2);
    RawProperty sin_port = raw(2, 2);
    RawProperty sin_addr = raw(4, 4);
    RawProperty sin_zero = raw(8, 8);
}
