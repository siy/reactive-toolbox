package org.reactivetoolbox.web.server.parameter.auth;

public enum AuthHeader {
    JWT("Bearer"),
    BASIC("Basic")
    ;
    private final String prefix;

    AuthHeader(final String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }
}
