package org.reactivetoolbox.io.async.net.lifecycle;

import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.net.context.ConnectionContext;
import org.reactivetoolbox.io.async.net.context.ReadConnectionContext;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;

import static org.reactivetoolbox.io.async.net.context.ReadConnectionContext.readConnectionContext;

//TODO: add handling timeout?
public class ReadWriteLifeCycle implements LifeCycle {
    private static final int DEFAULT_READ_BUFFER_SIZE = 16384;
    private static final Failure EOF = NativeError.ENODATA.asFailure();

    private final FN1<Promise<Unit>, ReadConnectionContext> handler;
    private final int bufferSize;
    private final Option<Timeout> timeout;

    private ReadWriteLifeCycle(final FN1<Promise<Unit>, ReadConnectionContext> handler,
                               final int bufferSize,
                               final Option<Timeout> timeout) {
        this.handler = handler;
        this.bufferSize = bufferSize;
        this.timeout = timeout;
    }

    public static ReadWriteLifeCycle readWrite(final FN1<Promise<Unit>, ReadConnectionContext> handler) {
        return readWrite(handler, DEFAULT_READ_BUFFER_SIZE);
    }

    public static ReadWriteLifeCycle readWrite(final FN1<Promise<Unit>, ReadConnectionContext> handler, final int bufferSize) {
        return readWrite(handler, bufferSize, Option.empty());
    }

    public static ReadWriteLifeCycle readWrite(final FN1<Promise<Unit>, ReadConnectionContext> handler, final Option<Timeout> timeout) {
        return readWrite(handler, DEFAULT_READ_BUFFER_SIZE, timeout);
    }

    public static ReadWriteLifeCycle readWrite(final FN1<Promise<Unit>, ReadConnectionContext> handler,
                                               final int bufferSize,
                                               final Option<Timeout> timeout) {
        return new ReadWriteLifeCycle(handler, bufferSize, timeout);
    }

    @Override
    public void process(final ConnectionContext connectionContext, final Promise<Unit> onClose) {
        final OffHeapBuffer buffer = OffHeapBuffer.fixedSize(bufferSize);

        onClose.thenDo(buffer::dispose);
        onClose.thenDo(() -> onClose.async(($, submitter) ->
                                                   submitter.closeFileDescriptor(connectionContext.clientConnection()
                                                                                                  .socket())));

        rwCycle(readConnectionContext(connectionContext, buffer), onClose);
    }

    private void rwCycle(final ReadConnectionContext connectionContext, final Promise<Unit> onClose) {
        Promise.<Tuple2<FileDescriptor, SizeT>>asyncPromise((promise, submitter) -> submitter.read(connectionContext.socket(), connectionContext.buffer(), timeout)
                                                                                             .chainTo(promise))
                .flatMap(tuple -> tuple.map((fd, sizeRead) -> sizeRead.value() > 0
                                                              ? handler.apply(connectionContext)
                                                              : Promise.readyFail(EOF)))
                .onSuccess($ -> rwCycle(connectionContext, onClose))
                .onFailure(onClose::fail);
    }
}
