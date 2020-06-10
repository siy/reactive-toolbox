package org.reactivetoolbox.io.uring;

import org.reactivetoolbox.io.raw.RawMemory;
import org.reactivetoolbox.io.uring.struct.raw.CompletionQueueEntry;

import java.util.function.Consumer;

class CompletionProcessor implements AutoCloseable {
    private static final long ENTRY_SIZE = 8L;    // each entry is a 64-bit pointer
    private final CompletionQueueEntry entry;
    private final long completionBuffer;
    private final int size;
    private boolean closed = false;

    private CompletionProcessor(final int numEntries) {
        size = numEntries;
        completionBuffer = RawMemory.allocate(size * ENTRY_SIZE);
        entry = CompletionQueueEntry.at(0);
    }

    public static CompletionProcessor create(final int numEntries) {
        return new CompletionProcessor(numEntries);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        RawMemory.dispose(completionBuffer);
        closed = true;
    }

    public CompletionProcessor process(final long baseAddress, final Consumer<CompletionQueueEntry> consumer) {
        long ready = 0;

        try {
            ready = Uring.peekCQ(baseAddress, completionBuffer, size);
            for (long i = 0, address = completionBuffer; i < ready; i++, address += ENTRY_SIZE) {
                entry.reposition(RawMemory.getLong(address));
                consumer.accept(entry);
            }
        } finally {
            if (ready > 0) {
                Uring.advanceCQ(baseAddress, ready);
            }
        }
        return this;
    }
}
