package org.reactivetoolbox.web.server.http;

public enum Headers implements HeaderName {
    AUTHORIZATION("Authorization"),
    ;

    private final String header;

    Headers(final String header) {
        this.header = header;
    }

    @Override
    public String header() {
        return header;
    }
}
