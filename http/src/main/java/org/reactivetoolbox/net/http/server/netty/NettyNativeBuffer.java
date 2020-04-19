package org.reactivetoolbox.net.http.server.netty;

import io.netty.buffer.ByteBuf;
import org.reactivetoolbox.net.http.server.NativeBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NettyNativeBuffer implements NativeBuffer {
    private final ByteBuf buffer;

    private NettyNativeBuffer(final ByteBuf buffer) {
        this.buffer = buffer;
    }

    public static NettyNativeBuffer buffer(final ByteBuf buffer) {
        return new NettyNativeBuffer(buffer);
    }

    public ByteBuf unwrap() {
        return buffer;
    }

    @Override
    public NettyNativeBuffer write(final String value) {
        buffer.writeBytes(value.getBytes(UTF_8));
        return this;
    }

    @Override
    public String asString() {
        return buffer.toString(UTF_8);
    }

    @Override
    public void release() {
        buffer.release();
    }
}
