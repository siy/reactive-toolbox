package org.reactivetoolbox.net.http.server.router;

public enum AuthType {
    JWT, BASIC;

    public String headerName() {
        return "Authorization";
    }
}
