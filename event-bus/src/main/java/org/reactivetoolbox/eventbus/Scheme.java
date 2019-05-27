package org.reactivetoolbox.eventbus;

import org.reactivetoolbox.core.functional.Either;

public enum Scheme {
    HTTP("http"),
    WS("ws"),
    SERVICE("service");

    private final String name;

    private Scheme(final String name) {
        this.name = name;
    }

    public Either<? extends EventBusError, Scheme> byName(final String name) {
        for(final Scheme scheme : Scheme.values()) {
            if (scheme.name.equals(name)) {
                return Either.success(scheme);
            }
        }
        return Either.failure(EventBusError.UNKNOWN_SCHEME);
    }
}
