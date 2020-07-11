package org.reactivetoolbox.io.async.lifecycle;

import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.context.ConnectionContext;
import org.reactivetoolbox.io.async.net.ServerConnector;
import org.reactivetoolbox.io.async.net.SocketFlag;

import java.util.function.Consumer;

import static org.reactivetoolbox.io.async.lifecycle.Repeat.REPEAT;

public class AcceptLifeCycle extends AbstractRepeatingLifeCycle {
    private final ServerConnector<?> connector;
    private final Consumer<ConnectionContext> consumer;

    private AcceptLifeCycle(final ServerConnector<?> connector, final Consumer<ConnectionContext> consumer) {
        this.connector = connector;
        this.consumer = consumer;
    }

    @Override
    protected Promise<Repeat> repeat() {
        return Promise.asyncPromise((promise, submitter) -> submitter.accept(connector.socket(), SocketFlag.closeOnExec())
                                                                     .map(connection -> ConnectionContext.context(connection.socket(),
                                                                                                             connector.address(),
                                                                                                             connection.address()))
                                                                     .onSuccess(consumer)
                                                                     .map($ -> REPEAT)
                                                                     .onResult(promise::resolve));
    }
}
