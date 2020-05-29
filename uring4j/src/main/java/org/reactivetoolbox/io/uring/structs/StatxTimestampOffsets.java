package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

interface StatxTimestampOffsets {
    int SIZE = 16;
    RawProperty tv_sec = raw(0, 8);
    RawProperty tv_nsec = raw(8, 4);
}