package org.reactivetoolbox.io.async.lifecycle;

import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.reactivetoolbox.io.async.lifecycle.Repeat.END;

/**
 * Simple "reader" life cycle. It repeats the same sequence of operations: read portion of data
 * from provided file descriptor and then pass read data to provided processor. Processing result then controls if this
 * sequence should be repeated or terminated.
 */
public class ReaderLifeCycle implements LifeCycle<Repeat> {
    private final FN1<Promise<Repeat>, OffHeapBuffer> processor;
    private final FileDescriptor fileDescriptor;
    private final Option<Timeout> timeout;
    private final OffHeapBuffer buffer;
    private final AtomicBoolean stopping = new AtomicBoolean(false);

    private ReaderLifeCycle(final FN1<Promise<Repeat>, OffHeapBuffer> processor,
                            final FileDescriptor fileDescriptor,
                            final int bufferSize,
                            final Option<Timeout> timeout) {
        this.processor = processor;
        this.fileDescriptor = fileDescriptor;
        this.timeout = timeout;
        this.buffer = OffHeapBuffer.fixedSize(bufferSize);
    }

    @Override
    public Promise<Repeat> stop() {
        stopping.compareAndSet(false, true);
        return Promise.readyOk(END);
    }

    @Override
    public Promise<Repeat> transition(final Repeat state) {
        return switch (state) {
            case END -> Promise.readyOk(END)
                               .onResult($ -> buffer.dispose());

            case REPEAT -> stopping.get() ? Promise.readyOk(END)
                                          : repeat();
        };
    }

    private Promise<Repeat> repeat() {
        return Promise.doAsync((promise, submitter) -> submitter.read(fileDescriptor, buffer, timeout)
                                                                .andThen(this::processInput)
                                                                .onFailure($ -> buffer.dispose())
                                                                .onResult(promise::resolve));
    }

    private Promise<Repeat> processInput(final Tuple2<FileDescriptor, SizeT> readResult) {
        return readResult.map((fd, sz) -> processor.apply(buffer));
    }
}
