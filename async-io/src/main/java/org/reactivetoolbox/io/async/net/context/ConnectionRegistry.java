package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.core.lang.support.ULID;

import java.util.concurrent.ConcurrentMap;

public interface ConnectionRegistry {
    ConnectionRegistry add(final IncomingConnectionContext incomingConnectionContext);

    ConnectionRegistry remove(final IncomingConnectionContext incomingConnectionContext);

    static ConnectionRegistry withMap(final ConcurrentMap<ULID, IncomingConnectionContext> connections) {
        return new ConnectionRegistry() {
            @Override
            public ConnectionRegistry add(final IncomingConnectionContext incomingConnectionContext) {
                connections.putIfAbsent(incomingConnectionContext.id(), incomingConnectionContext);
                return this;
            }

            @Override
            public ConnectionRegistry remove(final IncomingConnectionContext incomingConnectionContext) {
                connections.remove(incomingConnectionContext.id(), incomingConnectionContext);
                return this;
            }
        };
    }
}
