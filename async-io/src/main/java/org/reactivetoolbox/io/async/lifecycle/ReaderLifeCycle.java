package org.reactivetoolbox.io.async.lifecycle;

import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;

/**
 * Simple "reader" life cycle. It repeats the same sequence of operations: read portion of data from provided file descriptor and then pass read data to provided processor.
 * Processing result then controls if this sequence should be repeated or terminated.
 */
public class ReaderLifeCycle extends AbstractRepeatingLifeCycle {
    public static final int DEFAULT_BUFFER_SIZE = 32768;
    public static final int MIN_BUFFER_SIZE = 1024;

    private final FN1<Promise<Repeat>, OffHeapBuffer> processor;
    private final FileDescriptor fileDescriptor;
    private final Option<Timeout> timeout;
    private final OffHeapBuffer buffer;

    private ReaderLifeCycle(final FN1<Promise<Repeat>, OffHeapBuffer> processor,
                            final FileDescriptor fileDescriptor,
                            final int bufferSize,
                            final Option<Timeout> timeout) {
        this.processor = processor;
        this.fileDescriptor = fileDescriptor;
        this.timeout = timeout;
        this.buffer = OffHeapBuffer.fixedSize(Math.max(bufferSize, MIN_BUFFER_SIZE));
        stopPromise().onResult($ -> buffer.dispose());
    }

    public static ReaderLifeCycle reader(final FN1<Promise<Repeat>, OffHeapBuffer> processor,
                                         final FileDescriptor fileDescriptor,
                                         final int bufferSize,
                                         final Option<Timeout> timeout) {
        return new ReaderLifeCycle(processor, fileDescriptor, bufferSize, timeout);
    }

    protected Promise<Repeat> repeat() {
        return Promise.asyncPromise((promise, submitter) -> submitter.read(fileDescriptor, buffer, timeout)
                                                                     .syncFlatMap(this::processInput)
                                                                     .onResult(promise::syncResolve));
    }

    private Promise<Repeat> processInput(final Tuple2<FileDescriptor, SizeT> readResult) {
        return readResult.map((fd, sz) -> processor.apply(buffer));
    }
}
