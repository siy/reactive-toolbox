package org.reactivetoolbox.io.async.net;

/**
 * Socket types.
 */
public enum SocketType {
    STREAM(1),       // Provides sequenced, reliable, two-way, connection-based byte streams.
    DGRAM(2),        // Supports datagrams (connectionless, unreliable messages of a fixed maximum length).
    RAW(3),          // Provides raw network protocol access.
    RDM(4),          // Provides a reliable datagram layer that does not guarantee ordering.
    SEQPACKET(5),    // Provides a sequenced, reliable, two-way connection-based data transmission path for datagrams of fixed maximum length
    ;
    private final int code;

    SocketType(final int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
