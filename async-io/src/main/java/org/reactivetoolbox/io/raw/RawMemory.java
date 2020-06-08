package org.reactivetoolbox.io.raw;

import jdk.internal.misc.Unsafe;

public final class RawMemory {
    private static final Unsafe instance = Unsafe.getUnsafe();
    private RawMemory() {}

    // Access with enforced network (big-endian) byte order
    public static short getShortInNetOrder(final long address) {
        return instance.getShortUnaligned(null, address, true);
    }

    public static void putShortInNetOrder(final long address, final short value) {
        instance.putShortUnaligned(null, address, value, true);
    }

    public static int getIntInNetOrder(final long address) {
        return instance.getIntUnaligned(null, address, true);
    }

    public static void putIntInNetOrder(final long address, final int value) {
        instance.putIntUnaligned(null, address, value, true);
    }

    // Raw access of different size
    public static long getLong(final long address) {
        return instance.getLong(address);
    }

    public static int getInt(final long address) {
        return instance.getInt(address);
    }

    public static short getShort(final long address) {
        return instance.getShort(address);
    }

    public static byte getByte(final long address) {
        return instance.getByte(address);
    }

    public static byte[] getByteArray(final long address, final int length) {
        final byte[] output = new byte[length];
        instance.copyMemory(null, address, output, Unsafe.ARRAY_BYTE_BASE_OFFSET, length);
        return output;
    }

    public static void putLong(final long address, final long value) {
        instance.putLong(address, value);
    }

    public static void putInt(final long address, final int value) {
        instance.putInt(address, value);
    }

    public static void putShort(final long address, final short value) {
        instance.putShort(address, value);
    }

    public static void putByte(final long address, final byte value) {
        instance.putByte(address, value);
    }

    public static void putByteArray(final long address, final byte[] input) {
        putByteArray(address, input, input.length);
    }

    public static void putByteArray(final long address, final byte[] input, int maxLen) {
        int len = Math.min(maxLen, input.length);
        instance.copyMemory(input, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, len);
    }

    // Memory management
    public static long allocate(final long amount) {
        return instance.allocateMemory(amount);
    }

    public static long reallocate(final long address, final long newAmount) {
        return instance.reallocateMemory(address, newAmount);
    }

    public static void dispose(final long address) {
        instance.freeMemory(address);
    }

    public static void clear(final long address, final long size) {
        instance.setMemory(address, size, (byte) 0);
    }
}
