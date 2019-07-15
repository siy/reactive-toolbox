package org.reactivetoolbox.eventbus;

public class Route<T> {
    private final Path path;
    private final Handler<?, T> handler;

    private Route(final Path path, final Handler<?, T> handler) {
        this.path = path;
        this.handler = handler;
    }

    public static <R> Route<R> of(final String path, final Handler<?, R> handler) {
        return of(Path.of(path), handler);
    }

    public static <R> Route<R> of(final Path path, final Handler<?, R> handler) {
        return new Route<>(path, handler);
    }

    public Path path() {
        return path;
    }

    public Handler<?, T> handler() {
        return handler;
    }
}
