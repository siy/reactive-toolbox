package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;

public class ReadConnectionContext {
    private final IncomingConnectionContext incomingConnectionContext;
    private final OffHeapBuffer buffer;
    private final Promise<Unit> onClose;

    private ReadConnectionContext(final IncomingConnectionContext incomingConnectionContext,
                                  final OffHeapBuffer buffer,
                                  final Promise<Unit> onClose) {
        this.incomingConnectionContext = incomingConnectionContext;
        this.buffer = buffer;
        this.onClose = onClose;
    }

    public static ReadConnectionContext readConnectionContext(final IncomingConnectionContext incomingConnectionContext,
                                                              final OffHeapBuffer buffer,
                                                              final Promise<Unit> onClose) {
        return new ReadConnectionContext(incomingConnectionContext, buffer, onClose);
    }

    public IncomingConnectionContext connectionContext() {
        return incomingConnectionContext;
    }

    public OffHeapBuffer buffer() {
        return buffer;
    }

    public FileDescriptor socket() {
        return incomingConnectionContext.clientConnection().socket();
    }

    public Promise<Unit> onClose() {
        return onClose;
    }

    public CoreLogger logger() {
        return onClose.logger();
    }
}
