package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.io.async.file.FileDescriptor;
import org.reactivetoolbox.io.async.util.OffHeapBuffer;

public class ReadConnectionContext {
    final ConnectionContext connectionContext;
    final OffHeapBuffer buffer;

    private ReadConnectionContext(final ConnectionContext connectionContext, final OffHeapBuffer buffer) {
        this.connectionContext = connectionContext;
        this.buffer = buffer;
    }

    public static ReadConnectionContext readConnectionContext(final ConnectionContext connectionContext, final OffHeapBuffer buffer) {
        return new ReadConnectionContext(connectionContext, buffer);
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
}
