package org.reactivetoolbox.io.uring.struct.shape;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

public interface SocketAddressIn6Offsets {
    int SIZE = 28;
    RawProperty sin6_family = raw(0, 2);
    RawProperty sin6_addr = raw(8, 16);
    RawProperty sin6_flowinfo = raw(4, 4);
    RawProperty sin6_port = raw(2, 2);
    RawProperty sin6_scope_id = raw(24, 4);
}
