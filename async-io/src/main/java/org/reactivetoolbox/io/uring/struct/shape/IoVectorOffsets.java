package org.reactivetoolbox.io.uring.struct.shape;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

public interface IoVectorOffsets {
    int SIZE = 16;
    RawProperty iov_base = raw(0, 8);
    RawProperty iov_len = raw(8, 8);
}