package org.reactivetoolbox.io.api.net;

public interface InetAddress {
    /**
     * Get byte representation of address in network byte order.
     *
     * @return byte representation of the address.
     */
    byte[] asBytes();
}
