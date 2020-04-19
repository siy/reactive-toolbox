package org.reactivetoolbox.net.http.server;

public interface NativeBuffer {
    //TODO: more methods
    NativeBuffer write(final String value);

    String asString();

    void release();
}
