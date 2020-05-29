package org.reactivetoolbox.io;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.scheduler.Timeout;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * Low level externally accessible API for submission of I/O operations.
 * <p>
 * WARNING: Several API's require {@link ByteBuffer} as a parameter. In order to use these API's {@link ByteBuffer} instances
 * must be allocated using {@link ByteBuffer#allocateDirect(int)} method. Unfortunately due to design of the {@link ByteBuffer} and derived
 * classes it is impossible to enforce this requirement in the API because {@code java.nio.DirectByteBuffer} is package private class.
 * Alternative could be use of {@link java.nio.MappedByteBuffer}, but there is no requirement to have these buffers tied to file descriptors or
 * be a mapped files. The {@link java.nio.MappedByteBuffer} sheds some more light on this issue (see comments inside class).
 */
//TODO: add missing methods (see list at the end), cleanup, extract enums, do sanity checks (make sure port/address passed properly)
public interface Submitter {
    /**
     * Submit NOP operation.
     * This operation actually does nothing except roundtrip to OS kernel and back.
     *
     * @param completionHandler
     *         Callback which will be invoked on completion.
     */
    Submitter nop(final Consumer<NativeError> completionHandler);

    /**
     * Submit SPLICE operation.
     * Copies data from one file descriptor to another.
     *
     * @param fdIn
     *         Input file handle.
     * @param offsetIn
     *         Input offset. Must be 0 if input file handle points to a pipe/socket.
     * @param fdOut
     *         Output file handle.
     * @param offsetOut
     *         Output offset. Must be 0 if output file handle points to a pipe/socket.
     * @param count
     *         Number of bytes to copy.
     * @param flags
     *         Additional flags for the operation (see {@link SpliceFlags} for more details).
     * @param completionHandler
     *         Callback which will be invoked on completion.
     * @param timeout
     *         Optional operation timeout.
     */
    Submitter splice(final FileDescriptor fdIn, final long offsetIn,
                     final FileDescriptor fdOut, final long offsetOut,
                     final int count, final EnumSet<SpliceFlags> flags,
                     final Consumer<NativeError> completionHandler,
                     final Option<Timeout> timeout);

    /**
     * Same as {@link #splice(FileDescriptor, long, FileDescriptor, long, int, EnumSet, Consumer, Option)} except
     * no timeout is specified.
     */
    default Submitter splice(final FileDescriptor fdIn, final long offsetIn,
                             final FileDescriptor fdOut, final long offsetOut,
                             final int count, final EnumSet<SpliceFlags> flags,
                             final Consumer<NativeError> completionHandler) {
        return splice(fdIn, offsetIn, fdOut, offsetOut, count, flags, completionHandler, Option.empty());
    }

    enum SpliceFlags implements Bitmask {
        MOVE(1),        /* SPLICE_F_MOVE    : Move pages instead of copying.  */
        NONBLOCK(2),    /* SPLICE_F_NONBLOCK: Don't block on the pipe splicing */
        MORE(4);        /* SPLICE_F_MORE    : Expect more data.  */

        private final int mask;

        SpliceFlags(final int mask) {
            this.mask = mask;
        }

        @Override
        public int mask() {
            return mask;
        }
    }

    /**
     * Submit READ operation.
     * Read from specified file handle. The number of bytes to read is defined by the provided buffer {@link ByteBuffer#limit()}.
     * Upon successful completion but before {@code completionHandler} is invoked, {@code buffer} limit is set to number of bytes actually
     * read and the {@code buffer} position is set to zero. This makes buffer ready for further processing. If operation was nos successful,
     * then buffer is left intact.
     *
     * @param fdIn
     *         File handle to read from.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset to read from if file handle points to file.
     * @param completionHandler
     *         Callback which will be invoked on completion.
     * @param timeout
     *         Optional operation timeout.
     */
    Submitter read(final FileDescriptor fdIn,
                   final ByteBuffer buffer,
                   final long offset,
                   final Consumer<NativeError> completionHandler,
                   final Option<Timeout> timeout);

    /**
     * Same as {@link #read(FileDescriptor, ByteBuffer, long, Consumer, Option)} except no timeout is specified.
     */
    default Submitter read(final FileDescriptor fdIn,
                           final ByteBuffer buffer,
                           final long offset,
                           final Consumer<NativeError> completionHandler) {
        return read(fdIn, buffer, offset, completionHandler, Option.empty());
    }

    /**
     * Same as {@link #read(FileDescriptor, ByteBuffer, long, Consumer, Option)} except no offset is specified.
     * Convenient for using with sockets or reading file at current position.
     */
    default Submitter read(final FileDescriptor fdIn,
                           final ByteBuffer buffer,
                           final Consumer<NativeError> completionHandler,
                           final Option<Timeout> timeout) {
        return read(fdIn, buffer, 0L, completionHandler, timeout);
    }

    /**
     * Same as {@link #read(FileDescriptor, ByteBuffer, long, Consumer, Option)} except no offset and no timeout is specified.
     * Convenient for using with sockets or reading file at current position.
     */
    default Submitter read(final FileDescriptor fdIn,
                           final ByteBuffer buffer,
                           final Consumer<NativeError> completionHandler) {
        return read(fdIn, buffer, 0L, completionHandler, Option.empty());
    }

    /**
     * Submit WRITE operation.
     * Writes data into specified file handle at specified offset. The number of bytes to write is defined by the provided buffer
     * {@link ByteBuffer#limit()} and {@link ByteBuffer#position()} - bytes from {@link ByteBuffer#position()} to {@link ByteBuffer#limit()}
     * are written. Buffer remains intact regardless from the success of operation.
     *
     * @param fdOut
     *         File handle to write to.
     * @param buffer
     *         Data buffer.
     * @param offset
     *         Offset in a file to start writing if file handle points to file.
     * @param completionHandler
     *         Callback which will be invoked on completion.
     * @param timeout
     *         Optional operation timeout.
     */
    Submitter write(final FileDescriptor fdOut,
                    final ByteBuffer buffer,
                    final long offset,
                    final Consumer<NativeError> completionHandler,
                    final Option<Timeout> timeout);

    /**
     * Same as {@link #write(FileDescriptor, ByteBuffer, long, Consumer, Option)} except no timeout is specified.
     */
    default Submitter write(final FileDescriptor fdOut,
                            final ByteBuffer buffer,
                            final long offset,
                            final Consumer<NativeError> completionHandler) {
        return write(fdOut, buffer, offset, completionHandler, Option.empty());
    }

    /**
     * Same as {@link #write(FileDescriptor, ByteBuffer, long, Consumer, Option)} except no offset is specified.
     * Convenient for using with sockets or writing file at current position.
     */
    default Submitter write(final FileDescriptor fdOut,
                            final ByteBuffer buffer,
                            final Consumer<NativeError> completionHandler,
                            final Option<Timeout> timeout) {
        return write(fdOut, buffer, 0L, completionHandler, timeout);
    }

    /**
     * Same as {@link #write(FileDescriptor, ByteBuffer, long, Consumer, Option)} except no offset and no timeout is specified.
     * Convenient for using with sockets or writing file at current position.
     */
    default Submitter write(final FileDescriptor fdOut,
                            final ByteBuffer buffer,
                            final Consumer<NativeError> completionHandler) {
        return write(fdOut, buffer, 0L, completionHandler, Option.empty());
    }

    /**
     * Submit CLOSE operation.
     * Closes specified file handle (either file or socket).
     *
     * @param fd
     *         File handle to close.
     * @param timeout
     *         Optional operation timeout.
     * @param completionHandler
     *         Callback which will be invoked on completion.
     */
    Submitter close(final FileDescriptor fd, Option<Timeout> timeout, final Consumer<NativeError> completionHandler);

    /**
     * Same as {@link #close(FileDescriptor, Option)} except no timeout is specified.
     */
    default Submitter close(final FileDescriptor fd, final Consumer<NativeError> completionHandler) {
        return close(fd, Option.empty(), completionHandler);
    }

    /**
     * Submit OPEN operation.
     * Open file at specified location. Note that this method only partially covers functionality of the underlying {@code openat(2)} call. Instead
     * simple {@code open(2)} semantics is implemented.
     *
     * @param path
     *         File path.
     * @param flags
     *         File open flags.
     * @param mode
     *         File open mode. Must be present only if {@code flags} contains {@link OpenFlags#O_CREAT} or {@link OpenFlags#O_TMPFILE}.
     * @param completionHandler
     *         Callback which will be invoked on completion.
     */
    Submitter open(final Path path,
                   final EnumSet<OpenFlags> flags,
                   final EnumSet<OpenMode> mode,
                   final Consumer<Result<FileDescriptor>> completionHandler);

    enum OpenFlags implements Bitmask {
        O_RDONLY(00000000),
        O_WRONLY(00000001),
        O_RDWR(00000002),
        O_CREAT(00000100),
        O_EXCL(00000200),
        O_NOCTTY(00000400),
        O_TRUNC(00001000),
        O_APPEND(00002000),
        O_NONBLOCK(00004000),
        O_DSYNC(00010000),
        O_DIRECT(00040000),
        O_LARGEFILE(00100000),
        O_DIRECTORY(00200000),
        O_NOFOLLOW(00400000),
        O_NOATIME(01000000),
        O_CLOEXEC(02000000),
        O_SYNC((04000000 | 00010000)),
        O_PATH(010000000),
        O_TMPFILE((020000000 | 00200000)),
        O_NDELAY(00004000);

        private final int mask;

        OpenFlags(final int mask) {
            this.mask = mask;
        }

        @Override
        public int mask() {
            return mask;
        }
    }

    enum OpenMode implements Bitmask {
        S_IRWXU(00700), /* user (file owner) has read, write, and execute permission */
        S_IRUSR(00400), /* user has read permission */
        S_IWUSR(00200), /* user has write permission */
        S_IXUSR(00100), /* user has execute permission */
        S_IRWXG(00070), /* group has read, write, and execute permission */
        S_IRGRP(00040), /* group has read permission */
        S_IWGRP(00020), /* group has write permission */
        S_IXGRP(00010), /* group has execute permission */
        S_IRWXO(00007), /* others have read, write, and execute permission */
        S_IROTH(00004), /* others have read permission */
        S_IWOTH(00002), /* others have write permission */
        S_IXOTH(00001), /* others have execute permission */
        S_ISUID(04000), /* set-user-ID bit */
        S_ISGID(02000), /* set-group-ID bit */
        S_ISVTX(01000); /* sticky bit */

        private final int mask;

        OpenMode(final int mask) {
            this.mask = mask;
        }

        @Override
        public int mask() {
            return mask;
        }
    }

    /**
     * Create socket for making client-side connections/requests.
     *
     * @param flags
     *         Socket open flags (see {@link SocketOpenFlags} for more details)
     * @param completionHandler
     *         Callback which will be invoked on completion.
     */
    Submitter socket(final SocketDomain socketDomain,
                     final SocketType socketType,
                     final EnumSet<SocketFlag> openFlags,
                     final Consumer<Result<FileDescriptor>> completionHandler);

    enum SocketDomain {
        AF_UNIX(1),           // Local communication
        AF_LOCAL(1),          // Synonym for AF_UNIX
        AF_INET(2),           // IPv4 Internet protocols
        AF_AX25(3),           // Amateur radio AX.25 protocol
        AF_IPX(4),            // IPX - Novell protocols
        AF_APPLETALK(5),      // AppleTalk
        AF_X25(9),            // ITU-T X.25 / ISO-8208 protocol
        AF_INET6(10),          // IPv6 Internet protocols
        AF_DECnet(12),         // DECet protocol sockets
        AF_KEY(15),            // Key management protocol, originally developed for usage with IPsec
        AF_NETLINK(16),        // Kernel user interface device
        AF_PACKET(17),         // Low-level packet interface
        AF_RDS(21),            // Reliable Datagram Sockets (RDS) protocol
        AF_PPPOX(24),          // Generic PPP transport layer, for setting up L2 tunnels (L2TP and PPPoE)
        AF_LLC(26),            // Logical link control (IEEE 802.2 LLC) protocol
        AF_IB(27),             // InfiniBand native addressing
        AF_MPLS(28),           // Multiprotocol Label Switching
        AF_CAN(29),            // Controller Area Network automotive bus protocol
        AF_TIPC(30),           // TIPC, cluster domain sockets protocol
        AF_BLUETOOTH(31),      // Bluetooth low-level socket protocol
        AF_ALG(38),            // Interface to kernel crypto API
        AF_VSOCK(40),          // VSOCK protocol for hypervisor-guest communication
        AF_KCM(41),            // KCM (kernel connection multiplexor) interface
        AF_XDP(44),            // XDP (express data path) interface
        ;

        private final int code;

        SocketDomain(final int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }
    }

    /**
     * Socket types.
     */
    enum SocketType {
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

    /**
     * Socket open/accept flags.
     */
    enum SocketFlag implements Bitmask {
        SOCK_CLOEXEC(0x00080000),     /* Set close-on-exec flag for the descriptor */
        SOCK_NONBLOCK(0x00000800);    /* Mark descriptor as non-blocking */

        private final int mask;

        SocketFlag(final int mask) {
            this.mask = mask;
        }

        @Override
        public int mask() {
            return mask;
        }
    }

    /**
     * TODO: Change API to something more generic?
     * Create server socket bound to specified address and listening for incoming connection.
     * The remaining operation which need to be done for this socket is {@link}.
     *
     * @param flags
     *         Socket open flags (see {@link SocketOpenFlags} for more details)
     * @param port
     *         Port to to bind
     * @param address
     *         Optional address to bind. If not specified then INADD_ANY is used (i.e. socket will listen on all local interfaces).
     * @param backLog
     *         Listen backlog queue length
     * @param completionHandler
     *         Callback which will be invoked on completion.
     */
    Submitter serverSocket(final FileDescriptor socket,
                           final int port,
                           final Option<InetAddress> address,
                           final int backLog,
                           final Consumer<Result<FileDescriptor>> completionHandler);

    /**
     * Submit ACCEPT operation.
     * Accept incoming connection for server socket. Accepted connection receives its own socket which
     * then need to be used to communicate (read/write) with particular client.
     *
     * @param socket
     *         Server socket to accept connections on.
     * @param flags
     *         Accept flags (see {@link SocketAcceptFlags} for more details)
     * @param completionHandler
     *         Callback which will be invoked on completion.
     */
    Submitter accept(final FileDescriptor socket, final EnumSet<SocketFlag> flags, final Consumer<Result<ClientConnection>> completionHandler);

    class ClientConnection {
        private final FileDescriptor handle;
        private final InetAddress address;

        protected ClientConnection(final FileDescriptor handle, final InetAddress address) {
            this.handle = handle;
            this.address = address;
        }

        public FileDescriptor handle() {
            return handle;
        }

        public InetAddress address() {
            return address;
        }
    }

    /**
     * Submit CONNECT operation.
     * Connect to external server at provided address (host/port).
     *
     * @param socket
     *         Socket to connect
     * @param address
     *         Address to connect to
     * @param completionHandler
     *         Callback which will be invoked on completion.
     */
    Submitter connect(final FileDescriptor socket, final int port, final InetAddress address, final Consumer<NativeError> completionHandler);

    //TODO: recv, send - implement later, when special cases handling will be necessary
    //TODO: statx, readv, writev.
}
