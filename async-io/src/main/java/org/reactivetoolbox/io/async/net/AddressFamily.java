package org.reactivetoolbox.io.async.net;

import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;

import static org.reactivetoolbox.io.NativeError.EPFNOSUPPORT;

public enum AddressFamily {
    UNIX(1),           // Local communication
    LOCAL(1),          // Synonym for AF_UNIX
    INET(2),           // IPv4 Internet protocols
    AX25(3),           // Amateur radio AX.25 protocol
    IPX(4),            // IPX - Novell protocols
    APPLETALK(5),      // AppleTalk
    X25(9),            // ITU-T X.25 / ISO-8208 protocol
    INET6(10),          // IPv6 Internet protocols
    DEC_NET(12),         // DECet protocol sockets
    KEY(15),            // Key management protocol, originally developed for usage with IPsec
    NETLINK(16),        // Kernel user interface device
    PACKET(17),         // Low-level packet interface
    RDS(21),            // Reliable Datagram Sockets (RDS) protocol
    PPPOX(24),          // Generic PPP transport layer, for setting up L2 tunnels (L2TP and PPPoE)
    LLC(26),            // Logical link control (IEEE 802.2 LLC) protocol
    IB(27),             // InfiniBand native addressing
    MPLS(28),           // Multiprotocol Label Switching
    CAN(29),            // Controller Area Network automotive bus protocol
    TIPC(30),           // TIPC, cluster domain sockets protocol
    BLUETOOTH(31),      // Bluetooth low-level socket protocol
    ALG(38),            // Interface to kernel crypto API
    VSOCK(40),          // VSOCK protocol for hypervisor-guest communication
    KCM(41),            // KCM (kernel connection multiplexor) interface
    XDP(44),            // XDP (express data path) interface
    ;

    private final short id;
    private static final AddressFamily[] values = AddressFamily.values();

    AddressFamily(final int id) {
        this.id = (short) id;
    }

    public static AddressFamily unsafeFromCode(final short family) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            int cmp = values[mid].id - family;
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return values[mid];
        }

        return null;
    }

    public static Result<AddressFamily> addressFamily(final short family) {
        return Option.option(unsafeFromCode(family))
                     .fold($ -> Result.fail(EPFNOSUPPORT.asFailure()), Result::ok);
    }

    public short familyId() {
        return id;
    }
}
