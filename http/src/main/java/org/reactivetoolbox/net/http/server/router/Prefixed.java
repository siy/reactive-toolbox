package org.reactivetoolbox.net.http.server.router;

public interface Prefixed<T> {
    T prefix(final String prefix);
}
