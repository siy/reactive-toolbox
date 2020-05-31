package org.reactivetoolbox.net.http.server;

import org.reactivetoolbox.io.async.Promise;

public interface Server<T> {
    Promise<Server<T>> start();

    Promise<Server<T>> stop();
}
