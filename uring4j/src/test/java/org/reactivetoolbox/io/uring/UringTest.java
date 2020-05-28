package org.reactivetoolbox.io.uring;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.io.raw.RawMemory;
import org.reactivetoolbox.io.uring.structs.CompletionQueueEntry;
import org.reactivetoolbox.io.uring.structs.SubmitQueueEntry;

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

            /*
             	io_uring_prep_rw(op: IORING_OP_NOP, sqe: sqe, fd: -1, addr: NULL, len: 0, offset: 0);
                static inline void io_uring_prep_rw(int op, struct io_uring_sqe *sqe, int fd,
                                       const void *addr, unsigned len,
                                        __u64 offset)

                sqe->opcode = op;
                sqe->flags = 0;
                sqe->ioprio = 0;
                sqe->fd = fd;
                sqe->off = offset;
                sqe->addr = (unsigned long) addr;
                sqe->len = len;
                sqe->rw_flags = 0;
                sqe->user_data = 0;
                sqe->__pad2[0] = sqe->__pad2[1] = sqe->__pad2[2] = 0;
            */

            entry.clear()
                 .opcode(AsyncOperation.IORING_OP_NOP.opcode())
                 .userData(0x0F00DFEEDL)
                 .fd(-1);

            final long num = Uring.submitAndWait(ringBase, 1);

            System.out.println("Num: " + num);

            final int peekRc = Uring.peekCQ(ringBase, completionBase, 1024);

            System.out.println("Peek rc: " + peekRc);

            final long ptr = RawMemory.getLong(completionBase);

            System.out.printf("Peek ptr: %08x\n", ptr);

            final CompletionQueueEntry cq = CompletionQueueEntry.at(ptr);

            System.out.printf(" CQ {user data: %08x, result: %04d, flags: %04d}\n", cq.userData(), cq.result(), cq.flags());

            Uring.advanceCQ(ringBase, 1);
        } finally {
            Uring.close(ringBase);
            RawMemory.release(ringBase);
            RawMemory.release(completionBase);
        }
    }
}