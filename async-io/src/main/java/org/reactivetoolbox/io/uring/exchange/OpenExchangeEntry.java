package org.reactivetoolbox.io.uring.exchange;

import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.uring.struct.offheap.OffHeapCString;
import org.reactivetoolbox.io.uring.struct.raw.SubmitQueueEntry;
import org.reactivetoolbox.io.uring.utils.PlainObjectPool;

import java.nio.file.Path;
import java.util.function.Consumer;

import static org.reactivetoolbox.io.uring.AsyncOperation.IORING_OP_OPENAT;

public class OpenExchangeEntry extends AbstractExchangeEntry<OpenExchangeEntry, FileDescriptor> {
    private static final int AT_FDCWD = -100; // Special value used to indicate the openat/statx functions should use the current working directory.

    private OffHeapCString rawPath;
    private byte flags;
    private int openFlags;
    private int mode;

    protected OpenExchangeEntry(final PlainObjectPool<OpenExchangeEntry> pool) {
        super(IORING_OP_OPENAT, pool);
    }

    @Override
    protected void doAccept(final int res, final int flags) {
        rawPath.dispose();
        rawPath = null;

        final var result = res < 0 ? NativeError.<FileDescriptor>result(res)
                                   : Result.ok(FileDescriptor.file(res));
        completion.accept(result);
    }

    @Override
    public SubmitQueueEntry apply(final SubmitQueueEntry entry) {
        return super.apply(entry)
                    .flags(flags)
                    .fd(AT_FDCWD)
                    .addr(rawPath.address())
                    .len(mode)
                    .openFlags(openFlags);
    }

    public OpenExchangeEntry prepare(final Consumer<Result<FileDescriptor>> completion,
                                     final Path path,
                                     final int openFlags,
                                     final int mode,
                                     final byte flags) {
        rawPath = OffHeapCString.cstring(path.toString());

        this.flags = flags;
        this.openFlags = openFlags;
        this.mode = mode;

        return super.prepare(completion);
    }
}
