package org.reactivetoolbox.io.uring.structs;

import org.reactivetoolbox.io.raw.RawMemory;
import org.reactivetoolbox.io.raw.RawProperty;

abstract class AbstractRawStructure<T extends RawStructure<?>> implements RawStructure<T> {
    private long address;
    private final int size;

    protected AbstractRawStructure(final long address, final int size) {
        this.address = address;
        this.size = size;
    }

    @Override
    public long address() {
        return address;
    }

    protected void address(final long address) {
        this.address = address;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T clear() {
        RawMemory.clear(address(), size());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    T putByte(final RawProperty property, final byte value) {
        RawMemory.putByte(address + property.offset(), value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    T putShort(final RawProperty property, final short value) {
        RawMemory.putShort(address + property.offset(), value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    T putInt(final RawProperty property, final int value) {
        RawMemory.putInt(address + property.offset(), value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    T putLong(final RawProperty property, final long value) {
        RawMemory.putLong(address + property.offset(), value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    T putShortInNetOrder(final RawProperty property, final short value) {
        RawMemory.putShortInNetOrder(address + property.offset(), value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    T putIntInNetOrder(final RawProperty property, final int value) {
        RawMemory.putIntInNetOrder(address + property.offset(), value);
        return (T) this;
    }

    byte getByte(final RawProperty property) {
        return RawMemory.getByte(address + property.offset());
    }

    short getShort(final RawProperty property) {
        return RawMemory.getShort(address + property.offset());
    }

    int getInt(final RawProperty property) {
        return RawMemory.getInt(address + property.offset());
    }

    long getLong(final RawProperty property) {
        return RawMemory.getLong(address + property.offset());
    }

    short getShortInNetOrder(final RawProperty property) {
        return RawMemory.getShortInNetOrder(address + property.offset());
    }

    int getIntInNetOrder(final RawProperty property) {
        return RawMemory.getIntInNetOrder(address + property.offset());
    }
}