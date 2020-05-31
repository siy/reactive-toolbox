package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawProperty;

import static org.reactivetoolbox.io.raw.RawProperty.raw;

interface CompletionQueueEntryOffsets {
    int SIZE = 16;
    RawProperty user_data = raw(0, 8);
    RawProperty res = raw(8, 4);
    RawProperty flags = raw(12, 4);
}
