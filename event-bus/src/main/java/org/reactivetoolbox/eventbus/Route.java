package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.async.Promises.Promise;
import org.reactivetoolbox.core.functional.Functions.FN1;

public class Route<T> {
    private final Path path;
    private final FN1<Promise, T> handler;

    private Route(final Path path, final FN1<Promise, T> handler) {
        this.path = path;
        this.handler = handler;
    }

    public static <T> Route<T> of(final Path path, final FN1<Promise, T> handler) {
        return new Route<>(path, handler);
    }

    public Path path() {
        return path;
    }

    public FN1<Promise, T> handler() {
        return handler;
    }
}
