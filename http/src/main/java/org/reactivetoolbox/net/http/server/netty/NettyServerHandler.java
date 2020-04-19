package org.reactivetoolbox.net.http.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import org.reactivetoolbox.core.lang.functional.Failure;
import org.reactivetoolbox.net.http.server.router.Router;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.valueOf;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.reactivetoolbox.net.http.ContentType.TEXT_PLAIN;

class NettyServerHandler extends SimpleChannelInboundHandler<Object> {
    private final Router router;
    private final ServerConfig config;

    NettyServerHandler(final Router router, final ServerConfig config) {
        this.router = router;
        this.config = config;
    }

    /**
     * Handles a new message.
     *
     * @param ctx
     *         The channel context.
     * @param msg
     *         The HTTP request message.
     */
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final Object msg) {
        if (!(msg instanceof FullHttpRequest)) {
            return;
        }

        final FullHttpRequest request = (FullHttpRequest) msg;

        if (HttpUtil.is100ContinueExpected(request)) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }

        //TODO: configurable error response format
        final var context = new NettyRequestContext(ctx, request, config);

        router.locate(context.method(), context.path())
              .whenPresent(route -> route.handler()
                                         .apply(context)
                                         .onSuccess(buffer -> context.writeResponse(OK,
                                                                                    route.outputType(),
                                                                                    ((NettyNativeBuffer) buffer).unwrap()))
                                         .onFailure(failure -> context.writeResponse(toStatus(failure),
                                                                                     TEXT_PLAIN,
                                                                                     serializeString(failure.message()))))
              .whenEmpty(() -> context.writeResponse(NOT_FOUND,
                                                     TEXT_PLAIN,
                                                     serializeString(NOT_FOUND.reasonPhrase())));
    }

    private static HttpResponseStatus toStatus(final Failure failure) {
        return valueOf(failure.type().code(), failure.message());
    }

    private static ByteBuf serializeString(final String value) {
        return Unpooled.wrappedBuffer(value.getBytes(UTF_8));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        ctx.close();
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
