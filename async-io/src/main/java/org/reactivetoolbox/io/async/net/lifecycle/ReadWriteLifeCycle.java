package org.reactivetoolbox.io.async.net.lifecycle;

import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.net.context.ConnectionContext;
import org.reactivetoolbox.io.async.net.context.ReadConnectionContext;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;

import static org.reactivetoolbox.core.lang.functional.Option.empty;
import static org.reactivetoolbox.io.async.net.context.ReadConnectionContext.readConnectionContext;

//TODO: add handling timeout?
public class ReadWriteLifeCycle implements LifeCycle {
    private static final int DEFAULT_READ_BUFFER_SIZE = 16384;
    private static final Failure EOF = NativeError.ENODATA.asFailure();

    private final FN1<Promise<?>, ReadConnectionContext> handler;
    private final int bufferSize;
    private final Option<Timeout> timeout;

    private ReadWriteLifeCycle(final FN1<Promise<?>, ReadConnectionContext> handler,
                               final int bufferSize,
                               final Option<Timeout> timeout) {
        this.handler = handler;
        this.bufferSize = bufferSize;
        this.timeout = timeout;
    }

    public static ReadWriteLifeCycle readWrite(final FN1<Promise<?>, ReadConnectionContext> handler) {
        return readWrite(handler, DEFAULT_READ_BUFFER_SIZE);
    }

    public static ReadWriteLifeCycle readWrite(final FN1<Promise<?>, ReadConnectionContext> handler, final int bufferSize) {
        return readWrite(handler, bufferSize, empty());
    }

    public static ReadWriteLifeCycle readWrite(final FN1<Promise<?>, ReadConnectionContext> handler, final Option<Timeout> timeout) {
        return readWrite(handler, DEFAULT_READ_BUFFER_SIZE, timeout);
    }

    public static ReadWriteLifeCycle readWrite(final FN1<Promise<?>, ReadConnectionContext> handler,
                                               final int bufferSize,
                                               final Option<Timeout> timeout) {
        return new ReadWriteLifeCycle(handler, bufferSize, timeout);
    }

    @Override
    public void process(final ConnectionContext connectionContext, final Promise<Unit> onClose) {
        final OffHeapBuffer buffer = OffHeapBuffer.fixedSize(bufferSize);

        onClose.thenDo(buffer::dispose);
        onClose.thenDo(() -> Promise.<Unit>asyncPromise((promise, submitter) ->
                                                                submitter.closeFileDescriptor(promise, connectionContext.socket(), empty())));

        rwCycle(readConnectionContext(connectionContext, buffer), onClose);
    }

    private void rwCycle(final ReadConnectionContext connectionContext, final Promise<Unit> onClose) {
        onClose.async((promise, submitter) -> doRead(onClose, connectionContext, submitter));
//        onClose.async((promise, submitter) -> submitter.read(readResult ->
//                                                                     readResult.onSuccessDo(() -> submitter.write(writeResult ->
//                                                                                                                          writeResult.onSuccessDo(() -> rwCycle(connectionContext,
//                                                                                                                                                                onClose))
//                                                                                                                                     .onFailure(onClose::fail),
//                                                                                                                  connectionContext.socket(),
//                                                                                                                  connectionContext.buffer(),
//                                                                                                                  OffsetT.ZERO, timeout))
//                                                                               .onFailure(onClose::fail),
//                                                             connectionContext.socket(),
//                                                             connectionContext.buffer(),
//                                                             OffsetT.ZERO,
//                                                             timeout));
//        onClose.async((promise, submitter) -> submitter.read(readResult -> readResult.onSuccessDo(() -> handler.apply(connectionContext)
//                                                                                                               .thenDo(() -> rwCycle(connectionContext, onClose)))
//                                                                                     .onFailure(onClose::fail),
//                                                             connectionContext.socket(),
//                                                             connectionContext.buffer(),
//                                                             OffsetT.ZERO,
//                                                             timeout));

//        Promise.<SizeT>asyncPromise((promise, submitter) -> submitter.read(promise,
//                                                                           connectionContext.socket(),
//                                                                           connectionContext.buffer(),
//                                                                           OffsetT.ZERO,
//                                                                           timeout)
//                                                                     .onSuccess($ -> rwCycle(connectionContext, onClose)))
//                .syncFlatMap(sizeRead -> handler.apply(connectionContext))
//                .onFailure(onClose::fail);
    }

    private void doRead(final Promise<Unit> onClose, final ReadConnectionContext connectionContext, final Submitter submitter) {
        submitter.read(readResult ->
                               readResult.onSuccessDo(() -> submitter.write(writeResult ->
                                                                                    writeResult.onSuccessDo(() -> doRead(onClose, connectionContext, submitter))
                                                                                               .onFailure(onClose::fail),
                                                                            connectionContext.socket(),
                                                                            connectionContext.buffer(),
                                                                            OffsetT.ZERO, timeout))
                                         .onFailure(onClose::fail),
                       connectionContext.socket(),
                       connectionContext.buffer(),
                       OffsetT.ZERO,
                       timeout);
    }
}
