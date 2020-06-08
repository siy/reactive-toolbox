package org.reactivetoolbox.io.uring.struct.shape;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

public interface TimeSpecOffsets {
    int SIZE = 16;
    RawProperty tv_sec = raw(0, 8);
    RawProperty tv_nsec = raw(8, 8);
}