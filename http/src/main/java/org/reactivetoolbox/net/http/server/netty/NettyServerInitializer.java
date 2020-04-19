package org.reactivetoolbox.net.http.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.reactivetoolbox.core.lang.functional.Functions.FN0;
import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.functional.ThrowingFunctions;
import org.reactivetoolbox.net.http.server.ServerErrors;
import org.reactivetoolbox.net.http.server.router.Router;

import static org.reactivetoolbox.core.lang.functional.ThrowingFunctions.lift;

/**
 * The Initializer class initializes the HTTP channel.
 */
class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final FN0<Result<SelfSignedCertificate>> SSC_FACTORY = ThrowingFunctions.<SelfSignedCertificate>lift(SelfSignedCertificate::new);
    private static final FN1<Result<SslContext>, SelfSignedCertificate> CONTEXT_BUILDER =
            lift(certificate -> SslContextBuilder.forServer(certificate.certificate(),
                                                            certificate.privateKey())
                                                 .sslProvider(SslProvider.JDK)
                                                 .build());
    private final Router router;
    private final ServerConfig config;

    NettyServerInitializer(final Router router, final ServerConfig config) {
        this.router = router;
        this.config = config;
    }

    private Result<SslContext> createSelfSignedContext() {
        return config.enableSSL()
               ? SSC_FACTORY.apply().flatMap(CONTEXT_BUILDER)
               : Result.fail(ServerErrors.SSL_NOT_CONFIGURED);
    }

    /**
     * Initializes the channel pipeline with the HTTP response handlers.
     *
     * @param channel
     *         The Channel which was registered.
     */
    @Override
    public void initChannel(SocketChannel channel) {
        createSelfSignedContext()
                .onSuccess(sslContext -> channel.pipeline()
                                                .addLast(sslContext.newHandler(channel.alloc())));

        channel.pipeline()
               .addLast(new HttpRequestDecoder())
               .addLast(new HttpObjectAggregator(config.maxInputContentLength()))
               .addLast(new HttpResponseEncoder())
               .addLast(new HttpContentCompressor())
               //      .addLast(new ChunkedWriteHandler())
               .addLast("handler", new NettyServerHandler(router, config));
    }
}
