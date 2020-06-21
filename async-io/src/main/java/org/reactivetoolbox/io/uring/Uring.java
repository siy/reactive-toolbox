package org.reactivetoolbox.io.uring;

import org.reactivetoolbox.core.log.CoreLogger;
import org.reactivetoolbox.core.meta.AppMetaRepository;
import org.reactivetoolbox.io.async.net.AddressFamily;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.SocketType;
import org.reactivetoolbox.io.uring.struct.raw.RawSocketAddressIn;
import org.reactivetoolbox.io.uring.struct.raw.RawSocketAddressIn6;
import org.reactivetoolbox.io.uring.utils.LibraryLoader;

final class Uring {
    // Actual size of struct io_uring is 160 bytes at the moment of writing: May 2020
    public static final long SIZE = 256;

    static {
        try {
            LibraryLoader.fromJar("/liburingnative.so");
        } catch (final Exception e) {
            SingletonHolder.logger().error("Error while loading JNI library for Uring class: ", e);
            System.exit(-1);
        }
    }

    // Start/Stop
    public static native int init(int numEntries, long baseAddress, int flags);

    public static native void close(long baseAddress);

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
     *         Socket domain. Refer to {@link AddressFamily} for set of recognized values.
     * @param type
     *         Socket type and open flags. Refer to {@link SocketType} for possible types. The {@link SocketFlag} flags can be OR-ed if necessary.
     * @param options
     *         Socket option1s. Only subset of possible options are supported. Refer to {@link SocketOption} for details.
     * @return socket (>0) or error (<0)
     */
    public static native int socket(int domain, int type, int options);

    /**
     * Configure socket for listening at specified address, port and with specified depth of backlog queue. It's a combination of bind(2) and listen(2) calls.
     *
     * @param socket
     *         Socket to configure.
     * @param address
     *         Memory address with prepared socket address structure (See {@link RawSocketAddressIn} and {@link RawSocketAddressIn6} for more details}.
     * @param len
     *         Size of the prepared socket address structure.
     * @param queueDepth
     *         Set backlog queue dept.
     * @return 0 for success and negative value of error code in case of error.
     */
    public static native int prepareForListen(int socket, long address, int len, int queueDepth);

    private static final class SingletonHolder {
        private static final CoreLogger LOGGER = AppMetaRepository.instance().get(CoreLogger.class);

        static CoreLogger logger() {
            return LOGGER;
        }
    }
}
