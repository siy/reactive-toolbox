package org.reactivetoolbox.io.async.net.lifecycle;

import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Functions.FN3;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.net.context.ConnectionContext;
import org.reactivetoolbox.io.async.net.context.ReadConnectionContext;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;

import java.time.Clock;

import static org.reactivetoolbox.core.lang.functional.Option.empty;
import static org.reactivetoolbox.io.async.net.context.ReadConnectionContext.readConnectionContext;

//TODO: add handling timeout?
public class ReadWriteLifeCycle implements LifeCycle {
    private static final int DEFAULT_READ_BUFFER_SIZE = 16384;
    private static final Failure EOF = NativeError.ENODATA.asFailure();

    private final FN3<Promise<?>, ReadConnectionContext, SizeT, Submitter> handler;
    private final int bufferSize;
    private final Option<Timeout> timeout;

    private ReadWriteLifeCycle(final FN3<Promise<?>, ReadConnectionContext, SizeT, Submitter> handler,
                               final int bufferSize,
                               final Option<Timeout> timeout) {
        this.handler = handler;
        this.bufferSize = bufferSize;
        this.timeout = timeout;
    }

    public static ReadWriteLifeCycle readWrite(final FN3<Promise<?>, ReadConnectionContext, SizeT, Submitter> handler) {
        return readWrite(handler, DEFAULT_READ_BUFFER_SIZE);
    }

    public static ReadWriteLifeCycle readWrite(final FN3<Promise<?>, ReadConnectionContext, SizeT, Submitter> handler, final int bufferSize) {
        return readWrite(handler, bufferSize, empty());
    }

    public static ReadWriteLifeCycle readWrite(final FN3<Promise<?>, ReadConnectionContext, SizeT, Submitter> handler, final Option<Timeout> timeout) {
        return readWrite(handler, DEFAULT_READ_BUFFER_SIZE, timeout);
    }

    public static ReadWriteLifeCycle readWrite(final FN3<Promise<?>, ReadConnectionContext, SizeT, Submitter> handler,
                                               final int bufferSize,
                                               final Option<Timeout> timeout) {
        return new ReadWriteLifeCycle(handler, bufferSize, timeout);
    }

    @Override
    public void process(final ConnectionContext connectionContext, final Promise<Unit> onClose) {
        final OffHeapBuffer buffer = OffHeapBuffer.fixedSize(bufferSize);

        onClose.async($ -> System.out.println("Connection " + connectionContext.id() +
                           " opened at " + Clock.systemUTC().instant()));

        onClose.thenDo(buffer::dispose);
        onClose.thenDo(() -> Promise.<Unit>asyncPromise((promise, submitter) ->
                                                                submitter.closeFileDescriptor(promise, connectionContext.socket(), empty())
                                                                         .onResult(unitResult ->
                                                                                           System.out.println("Connection " + connectionContext.id() +
                                                                                                              " closed (" + unitResult + "), at " + Clock.systemUTC().instant()))));
        rwCycle(readConnectionContext(connectionContext, buffer, onClose));
    }

    private void rwCycle(final ReadConnectionContext connectionContext) {
        connectionContext.onClose()
                         .async((promise, submitter) -> doRead(connectionContext, submitter));
    }

    private void doRead(final ReadConnectionContext connectionContext, final Submitter submitter) {
        //TODO: candidate for cleanup
        submitter.read(readResult -> readResult.onFailure(failure -> System.err.println("Read error " + failure + " at " + Clock.systemUTC().instant()))
                                               .onFailure(connectionContext.onClose()::fail)
                                               //TODO: candidate for cleanup
                                               .onSuccess(bytesRead -> handler.apply(connectionContext, bytesRead, submitter)
                                                                              .onFailure(connectionContext.onClose()::fail))
                                                                              //.onFailure(connectionContext.onClose()::fail).releaseWhenDone())

                                               //TODO: candidate for cleanup
                                               .onSuccess($ -> doRead(connectionContext, submitter)),
                       //.onSuccess($ -> rwCycle(connectionContext))),
                       connectionContext.socket(),
                       connectionContext.buffer(),
                       OffsetT.ZERO,
                       timeout);
    }
}
