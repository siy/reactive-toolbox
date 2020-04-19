package org.reactivetoolbox.net.http.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.net.http.ContentType;
import org.reactivetoolbox.net.http.Method;
import org.reactivetoolbox.net.http.server.NativeBuffer;
import org.reactivetoolbox.net.http.server.ParsingContext;
import org.reactivetoolbox.net.http.server.RequestContext;
import org.reactivetoolbox.net.http.server.router.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.DATE;
import static io.netty.handler.codec.http.HttpHeaderNames.SERVER;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static org.reactivetoolbox.core.lang.functional.Suppliers.memoize;
import static org.reactivetoolbox.net.http.server.ParsingContext.*;
import static org.reactivetoolbox.net.http.server.netty.NettyNativeBuffer.buffer;
import static org.reactivetoolbox.net.http.server.router.Utils.*;

class NettyRequestContext implements RequestContext {
    private static final String SERVER_NAME = "Reactive Toolbox HTTP Server (Netty)";

    private final ChannelHandlerContext context;
    private final FullHttpRequest request;
    private final ServerConfig config;
    private final boolean keepAlive;
    private final QueryStringDecoder queryDecoder;
    private final Supplier<ParsingContext> headerContext;
    private final Supplier<ParsingContext> queryContext;
    private Supplier<ParsingContext> pathParameterContext;
    private final Method method;

    NettyRequestContext(final ChannelHandlerContext context, final FullHttpRequest request, final ServerConfig config) {
        this.context = context;
        this.request = request;
        //TODO: use safer method for conversion
        this.method = Method.valueOf(request.method().name());
        this.keepAlive = HttpUtil.isKeepAlive(request);
        this.config = config;
        this.queryDecoder = new QueryStringDecoder(request.uri());
        this.queryContext = memoize(() -> context(transform(queryDecoder.parameters())));
        this.headerContext = memoize(() -> context(transform(extractHeaders(request.headers()))));
    }

    private Map<String, java.util.List<String>> extractHeaders(final HttpHeaders headers) {
        final var map = new HashMap<String, java.util.List<String>>();

        headers.forEach(header -> map.compute(header.getKey(), (key, oldValue) -> listCombiner(key, oldValue, header.getValue())));

        return map;
    }

    public void writeResponse(final HttpResponseStatus status,
                              final ContentType contentType,
                              final ByteBuf content) {

        final var response = buildResponse(status, contentType, content);

        context.writeAndFlush(response)
               .addListener(keepAlive
                            ? (ch) -> {}
                            : ChannelFutureListener.CLOSE);
    }

    private FullHttpResponse buildResponse(final HttpResponseStatus status,
                                           final ContentType contentType,
                                           final ByteBuf content) {
        final var response = new DefaultFullHttpResponse(HTTP_1_1, status, content, false);

        setKeepAliveHeader(response.headers()).set(SERVER, SERVER_NAME)
                                              .set(DATE, now(UTC).format(RFC_1123_DATE_TIME))
                                              .set(CONTENT_TYPE, contentType.get())
                                              .setInt(CONTENT_LENGTH, content.readableBytes());

        return response;
    }

    private HttpHeaders setKeepAliveHeader(final HttpHeaders headers) {
        if (keepAlive) {
            if (!request.protocolVersion().isKeepAliveDefault()) {
                headers.set(CONNECTION, KEEP_ALIVE);
            }
        } else {
            headers.set(CONNECTION, CLOSE);
        }

        return headers;
    }

    @Override
    public NativeBuffer allocate() {
        return buffer(context.alloc().buffer(config.initialBufferSize(),
                                             config.maxBufferSize()));
    }

    @Override
    public RequestContext preparePathContext(final FN1<ParsingContext, String> extractor) {
        pathParameterContext = memoize(extractor.bind(queryDecoder.path())::apply);
        return this;
    }

    @Override
    public ParsingContext pathContext() {
        return pathParameterContext.get();
    }

    @Override
    public ParsingContext queryContext() {
        return queryContext.get();
    }

    @Override
    public ParsingContext headerContext() {
        return headerContext.get();
    }

    @Override
    public String path() {
        return queryDecoder.path();
    }

    @Override
    public Method method() {
        return method;
    }

    //TODO: finish it
    @Override
    public ParsingContext bodyContext() {
        return null;
    }
}
