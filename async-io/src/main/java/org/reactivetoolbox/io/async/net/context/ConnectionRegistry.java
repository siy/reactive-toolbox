package org.reactivetoolbox.io.async.net.context;

import org.reactivetoolbox.core.lang.support.ULID;

import java.util.concurrent.ConcurrentMap;

public interface ConnectionRegistry {
    ConnectionRegistry add(final ConnectionContext connectionContext);

    ConnectionRegistry remove(final ConnectionContext connectionContext);

    static ConnectionRegistry withMap(final ConcurrentMap<ULID, ConnectionContext> connections) {
        return new ConnectionRegistry() {
            @Override
            public ConnectionRegistry add(final ConnectionContext connectionContext) {
                connections.putIfAbsent(connectionContext.id(), connectionContext);
                return this;
            }

            @Override
            public ConnectionRegistry remove(final ConnectionContext connectionContext) {
                connections.remove(connectionContext.id(), connectionContext);
                return this;
            }
        };
    }
}
