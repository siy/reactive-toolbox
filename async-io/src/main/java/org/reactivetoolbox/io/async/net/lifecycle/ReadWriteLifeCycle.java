package org.reactivetoolbox.io.async.net.lifecycle;

import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.core.lang.functional.Functions.FN2;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.NativeError;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.Submitter;
import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.net.context.IncomingConnectionContext;
import org.reactivetoolbox.io.async.net.context.ReadConnectionContext;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;
import org.reactivetoolbox.io.scheduler.Timeout;

import static org.reactivetoolbox.core.lang.functional.Option.empty;
import static org.reactivetoolbox.io.async.net.context.ReadConnectionContext.readConnectionContext;

public class ReadWriteLifeCycle implements LifeCycle {
    //            private static final int DEFAULT_READ_BUFFER_SIZE = 1024 * 1024;
    private static final int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;
    private static final Failure EOF = NativeError.ENODATA.asFailure();

    private final FN2<Promise<?>, ReadConnectionContext, SizeT> handler;
    private final int bufferSize;
    private final Option<Timeout> timeout;

    private ReadWriteLifeCycle(final FN2<Promise<?>, ReadConnectionContext, SizeT> handler,
                               final int bufferSize,
                               final Option<Timeout> timeout) {
        this.handler = handler;
        this.bufferSize = bufferSize;
        this.timeout = timeout;
    }

    public static ReadWriteLifeCycle readWrite(final FN2<Promise<?>, ReadConnectionContext, SizeT> handler) {
        return readWrite(handler, DEFAULT_READ_BUFFER_SIZE);
    }

    public static ReadWriteLifeCycle readWrite(final FN2<Promise<?>, ReadConnectionContext, SizeT> handler, final int bufferSize) {
        return readWrite(handler, bufferSize, empty());
    }

    public static ReadWriteLifeCycle readWrite(final FN2<Promise<?>, ReadConnectionContext, SizeT> handler, final Option<Timeout> timeout) {
        return readWrite(handler, DEFAULT_READ_BUFFER_SIZE, timeout);
    }

    public static ReadWriteLifeCycle readWrite(final FN2<Promise<?>, ReadConnectionContext, SizeT> handler,
                                               final int bufferSize,
                                               final Option<Timeout> timeout) {
        return new ReadWriteLifeCycle(handler, bufferSize, timeout);
    }

    public static Promise<SizeT> echo(final ReadConnectionContext context, final SizeT bytesRead) {
        return Promise.asyncPromise((onClose, submitter) -> submitter.write(context.socket(), context.buffer(), OffsetT.ZERO, empty())
                                                                     .onSuccess(bw -> context.logger().info("Wrote {0} bytes", bw))
                                                                     .onFailure(failure -> context.onClose()
                                                                                                  .logger()
                                                                                                  .info("Write error: {0}", failure))
                                                                     .onSuccess(bytesWritten -> {
                                                                         if (!bytesWritten.equals(bytesRead)) {
                                                                             context.logger()
                                                                                    .info("Write != Read, {0} != {1}, buffer {2}",
                                                                                          bytesWritten,
                                                                                          bytesRead,
                                                                                          context.buffer().used());
                                                                         }
                                                                     }));
    }

    @Override
    public void process(final IncomingConnectionContext incomingConnectionContext, final Promise<Unit> onClose) {
        final OffHeapBuffer buffer = OffHeapBuffer.fixedSize(bufferSize);
        final ReadConnectionContext context = readConnectionContext(incomingConnectionContext, buffer, onClose);

        //onClose.async($ -> context.logger().info("Connection {0} opened at {1}", incomingConnectionContext.id(), Clock.systemUTC().instant()));

        onClose.thenDo(buffer::dispose);
        onClose.thenDo(() -> Promise.<Unit>asyncPromise((promise, submitter) ->
                                                                submitter.closeFileDescriptor(promise,
                                                                                              incomingConnectionContext.socket(),
                                                                                              empty())));
        rwCycle(context);
    }
//TODO: rework using new Promise API's
    //    private void rwCycle(final ReadConnectionContext connectionContext) {
//        connectionContext.onClose()
//                         .async((promise, submitter) -> doRead(connectionContext, submitter));
//    }
//
//    private void doRead(final ReadConnectionContext connectionContext, final Submitter submitter) {
//        submitter.read(readResult -> readResult.onFailure(connectionContext.onClose()::fail)
//                                               .onSuccess(bytesRead -> doWrite(connectionContext, submitter)),
//                       connectionContext.socket(),
//                       connectionContext.buffer(),
//                       OffsetT.ZERO,
//                       timeout);
//    }
//
//    private void doWrite(final ReadConnectionContext connectionContext, final Submitter submitter) {
//        submitter.write(writeResult -> writeResult.onFailure(connectionContext.onClose()::fail)
//                                                  .onSuccess($ -> doRead(connectionContext, submitter)),
//                        connectionContext.socket(),
//                        connectionContext.buffer(),
//                        OffsetT.ZERO,
//                        timeout);
//    }
    private void rwCycle(final ReadConnectionContext connectionContext) {
        connectionContext.onClose()
                         .async((promise, submitter) ->
                                        doReadPromise(connectionContext, submitter));
    }

    private Promise<SizeT> doReadPromise(final ReadConnectionContext connectionContext, final Submitter submitter) {
        return submitter.read(connectionContext.socket(),
                              connectionContext.buffer(),
                              OffsetT.ZERO,
                              timeout);
    }

    private Promise<SizeT> doWritePromise(final ReadConnectionContext connectionContext, final Submitter submitter) {
        return submitter.write(connectionContext.socket(),
                               connectionContext.buffer(),
                               OffsetT.ZERO,
                               timeout);
    }
}
