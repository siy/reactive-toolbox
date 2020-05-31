package org.reactivetoolbox.io.api.net;

/**
 * Socket types.
 */
public enum SocketType {
    SOCK_STREAM(1),       // Provides sequenced, reliable, two-way, connection-based byte streams.
    SOCK_DGRAM(2),        // Supports datagrams (connectionless, unreliable messages of a fixed maximum length).
    SOCK_RAW(3),          // Provides raw network protocol access.
    SOCK_RDM(4),          // Provides a reliable datagram layer that does not guarantee ordering.
    SOCK_SEQPACKET(5),    // Provides a sequenced, reliable, two-way connection-based data transmission path for datagrams of fixed maximum length
    ;
    private final int code;

    SocketType(final int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
