package org.reactivetoolbox.io.api.net;

public enum AddressFamily {
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

    AddressFamily(final int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
