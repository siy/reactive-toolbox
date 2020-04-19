package org.reactivetoolbox.net.http.server.netty;

//TODO: structure it into subclasses
public class ServerConfig {
    public int maxInputContentLength() {
        return 65536;
    }

    public boolean enableSSL() {
        return false;
    }

    public int port() {
        return 8080;
    }

    public int initialBufferSize() {
        return 4096;
    }

    public int maxBufferSize() {
        return 1024 * initialBufferSize();
    }
}
