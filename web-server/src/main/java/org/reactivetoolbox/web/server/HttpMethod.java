package org.reactivetoolbox.web.server;

import org.reactivetoolbox.eventbus.PathKey;

public enum HttpMethod implements PathKey {
    GET, PUT, PATCH, POST, HEADER;

    @Override
    public String key() {
        return name();
    }
}
