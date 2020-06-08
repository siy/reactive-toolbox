package org.reactivetoolbox.io.uring;

import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.uring.struct.raw.RawSocketAddressIn;
import org.reactivetoolbox.io.uring.struct.raw.RawSocketAddressIn6;
import org.reactivetoolbox.io.uring.utils.LibraryLoader;

final class Uring {
    // Actual size of struct io_uring is 160 bytes at the moment of writing
    public static final long SIZE = 256;

    static {
        try {
            LibraryLoader.fromJar("/liburingnative.so");
        } catch (final Exception e) {
            System.err.println("Error while loading JNI library for Uring class: " + e);
            System.exit(-1);
        }
    }

    // Start/Stop
    public static native int init(int numEntries, long baseAddress, int flags);
    public static native void close(long baseAddress);

    public interface RingOpenFlags {
        int IORING_SETUP_IOPOLL     = (1 << 0);    /* io_context is polled */
        int IORING_SETUP_SQPOLL     = (1 << 1);    /* SQ poll thread */
        int IORING_SETUP_SQ_AFF     = (1 << 2);    /* sq_thread_cpu is valid */
        int IORING_SETUP_CQSIZE     = (1 << 3);    /* app defines CQ size */
        int IORING_SETUP_CLAMP      = (1 << 4);    /* clamp SQ/CQ ring sizes */
        int IORING_SETUP_ATTACH_WQ  = (1 << 5);    /* attach to existing wq */
    }

    // Completion
    public static native int peekCQ(long baseAddress, long completionsAddress, long count);
    public static native void advanceCQ(long baseAddress, long count);
    public static native int readyCQ(long baseAddress);

    // Submissions
    public static native long spaceLeft(long baseAddress);
    public static native long nextSQEntry(long baseAddress);
    public static native long submitAndWait(long baseAddress, int waitNr);

    // Socket API

    /**
     * Create socket. This call is a combination of socket(2) and setsockopt(2).
     *
     * @param domain
     *          Socket domain. Refer to {@link AddressFamily} for set of recognized values.
     * @param type
     *          Socket type and open flags. Refer to {@link SocketType} for possible types. The {@link SocketFlag} flags can be OR-ed if necessary.
     * @param options
     *          Socket option1s. Only subset of possible options are supported. Refer to {@link SocketOption} for details.
     * @return socket (>0) or error (<0)
     */
    public static native int socket(int domain, int type, int options);

    /**
     * Configure socket for listening at specified address, port and with specified depth of backlog queue.
     * It's a combination of bind(2) and listen(2) calls.
     *
     * @param socket
     *      Socket to configure.
     * @param address
     *      Memory address with prepared socket address structure (See {@link RawSocketAddressIn} and {@link RawSocketAddressIn6} for more details}.
     * @param len
     *      Size of the prepared socket address structure.
     * @param queueDepth
     *      Set backlog queue dept.
     * @return 0 for success and non-zero for error.
     */
    public static native int prepareForListen(int socket, long address, int len, int queueDepth);

//    /**
//     * Socket address families (domains).
//     */
//    public interface SocketDomain {
//        static final int AF_UNIX = 1;           // Local communication
//        static final int AF_LOCAL = 1;          // Synonym for AF_UNIX
//        static final int AF_INET = 2;           // IPv4 Internet protocols
//        static final int AF_AX25 = 3;           // Amateur radio AX.25 protocol
//        static final int AF_IPX = 4;            // IPX - Novell protocols
//        static final int AF_APPLETALK = 5;      // AppleTalk
//        static final int AF_X25 = 9;            // ITU-T X.25 / ISO-8208 protocol
//        static final int AF_INET6 = 10;          // IPv6 Internet protocols
//        static final int AF_DECnet = 12;         // DECet protocol sockets
//        static final int AF_KEY = 15;            // Key management protocol, originally developed for usage with IPsec
//        static final int AF_NETLINK = 16;        // Kernel user interface device
//        static final int AF_PACKET = 17;         // Low-level packet interface
//        static final int AF_RDS = 21;            // Reliable Datagram Sockets (RDS) protocol
//        static final int AF_PPPOX = 24;          // Generic PPP transport layer, for setting up L2 tunnels (L2TP and PPPoE)
//        static final int AF_LLC = 26;            // Logical link control (IEEE 802.2 LLC) protocol
//        static final int AF_IB = 27;             // InfiniBand native addressing
//        static final int AF_MPLS = 28;           // Multiprotocol Label Switching
//        static final int AF_CAN = 29;            // Controller Area Network automotive bus protocol
//        static final int AF_TIPC = 30;           // TIPC, cluster domain sockets protocol
//        static final int AF_BLUETOOTH = 31;      // Bluetooth low-level socket protocol
//        static final int AF_ALG = 38;            // Interface to kernel crypto API
//        static final int AF_VSOCK = 40;          // VSOCK protocol for hypervisor-guest communication
//        static final int AF_KCM = 41;            // KCM (kernel connection multiplexor) interface
//        static final int AF_XDP = 44;            // XDP (express data path) interface
//    }

//    /**
//     * Socket types.
//     */
//    public interface SocketType {
//        static final int SOCK_STREAM = 1;       // Provides sequenced, reliable, two-way, connection-based byte streams.
//        static final int SOCK_DGRAM = 2;        // Supports datagrams (connectionless, unreliable messages of a fixed maximum length).
//        static final int SOCK_SEQPACKET = 5;    // Provides a sequenced, reliable, two-way connection-based data transmission path for datagrams of fixed maximum length
//        static final int SOCK_RAW = 3;          // Provides raw network protocol access.
//        static final int SOCK_RDM = 4;          // Provides a reliable datagram layer that does not guarantee ordering.
//    }

//    /**
//     * Socket open flags.
//     */
//    public interface SocketOpenFlag {
//        static final int SOCK_NONBLOCK = 0x00000800;     // Set the O_NONBLOCK file status flag on the new file descriptor.
//        static final int SOCK_CLOEXEC  = 0x00080000;     // Set the close-on-exec (FD_CLOEXEC) flag on the new file descriptor.
//    }
//
//    /**
//     * Options which can be enabled for socket.
//     *
//     * Note that values do not match ones from C headers since here they are used as bit flags rather than separate options.
//     * Also, note that {@link SocketOption#SO_LINGER} allows only set zero linger time.
//     */
//    public interface SocketOption {
//        static final int SO_KEEPALIVE = 0x0001; // Enable keep-alive packets
//        static final int SO_REUSEADDR = 0x0002; // Enable reuse address option
//        static final int SO_REUSEPORT = 0x0004; // Enable reuse port option
//        static final int SO_LINGER    = 0x0008; // Enable linger option and set linger time to 0
//    }
}
