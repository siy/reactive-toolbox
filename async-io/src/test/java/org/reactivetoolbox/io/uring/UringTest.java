package org.reactivetoolbox.io.uring;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.io.raw.RawMemory;
import org.reactivetoolbox.io.uring.struct.raw.CompletionQueueEntry;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;

import static org.junit.jupiter.api.Assertions.*;

public class UringTest {
    @Test
    void nopCanBeSubmittedAndConfirmed() {
        final long ringBase = RawMemory.allocate(256);

        assertNotEquals(0, ringBase);

        final long completionBase = RawMemory.allocate(16 * 1024); // 1024 * 2 (CQ size is twice of SQ size) * 8 (bytes per ptr)

        final int rc = Uring.init(1024, ringBase, 0);

        try {
            assertEquals(0, rc);

            final long sq = Uring.nextSQEntry(ringBase);

            assertNotEquals(0, sq);

            final SubmitQueueEntry entry = SubmitQueueEntry.at(sq);

            entry.clear()
                 .opcode(AsyncOperation.IORING_OP_NOP.opcode())
                 .userData(0x0CAFEBABEL)
                 .fd(-1);

            final long completionCount = Uring.submitAndWait(ringBase, 1);

            assertEquals(1, completionCount);

            final int readyCompletions = Uring.peekCQ(ringBase, completionBase, 1024);

            assertEquals(1, readyCompletions);

            final CompletionQueueEntry cq = CompletionQueueEntry.at(RawMemory.getLong(completionBase));

            assertEquals(0x0CAFEBABEL, cq.userData());

            Uring.advanceCQ(ringBase, 1);
        } finally {
            Uring.close(ringBase);
            RawMemory.dispose(ringBase);
            RawMemory.dispose(completionBase);
        }
    }

    @Test
    void nopCanBeSubmittedAndConfirmedWithCompletionProcessor() {
        final long ringBase = RawMemory.allocate(256);

        assertNotEquals(0, ringBase);

        final int rc = Uring.init(1024, ringBase, 0);

        try {
            assertEquals(0, rc);

            final long sq = Uring.nextSQEntry(ringBase);

            assertNotEquals(0, sq);

            final SubmitQueueEntry entry = SubmitQueueEntry.at(sq);

            entry.clear()
                 .opcode(AsyncOperation.IORING_OP_NOP.opcode())
                 .userData(0x0CAFEBABEL)
                 .fd(-1);

            final long completionCount = Uring.submitAndWait(ringBase, 1);
            assertEquals(1, completionCount);

            try (final CompletionProcessor processor = CompletionProcessor.create(1024)) {
                processor.process(ringBase, (cq) -> assertEquals(0x0CAFEBABEL, cq.userData()));
            }
        } finally {
            Uring.close(ringBase);
            RawMemory.dispose(ringBase);
        }
    }
}