package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;

public class ReadConnectionContext {
    private final ConnectionContext connectionContext;
    private final OffHeapBuffer buffer;
    private final Promise<Unit> onClose;

    private ReadConnectionContext(final ConnectionContext connectionContext,
                                  final OffHeapBuffer buffer,
                                  final Promise<Unit> onClose) {
        this.connectionContext = connectionContext;
        this.buffer = buffer;
        this.onClose = onClose;
    }

    public static ReadConnectionContext readConnectionContext(final ConnectionContext connectionContext,
                                                              final OffHeapBuffer buffer,
                                                              final Promise<Unit> onClose) {
        return new ReadConnectionContext(connectionContext, buffer, onClose);
    }

    public ConnectionContext connectionContext() {
        return connectionContext;
    }

    public OffHeapBuffer buffer() {
        return buffer;
    }

    public FileDescriptor socket() {
        return connectionContext.clientConnection().socket();
    }

    public Promise<Unit> onClose() {
        return onClose;
    }
}
