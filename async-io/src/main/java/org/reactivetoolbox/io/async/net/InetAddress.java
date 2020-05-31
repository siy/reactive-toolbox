package org.reactivetoolbox.io.async.net;

public interface InetAddress {
    /**
     * Get byte representation of address in network byte order.
     *
     * @return byte representation of the address.
     */
    byte[] asBytes();
}
