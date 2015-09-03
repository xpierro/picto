package com.github.picto.network.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.ArrayUtils;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.util.function.Function;

/**
 * Created by Pierre on 31/08/15.
 */
public class GetExecutor {
    private static final String LOCALHOST = "127.0.0.1";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;

    private URI uri;

    private String scheme;
    private String host;
    private int port;

    private Bootstrap bootstrap;
    private SslContext sslCtx;

    private byte[] response;

    private Function<byte[], ?> onByteReceived;
    private Function<Void, Void> onConnectionClose;

    private final SimpleChannelInboundHandler<HttpObject> handler = new SimpleChannelInboundHandler<HttpObject>() {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if (msg instanceof HttpResponse) {
                HttpResponse response = (HttpResponse) msg;

                if (!response.headers().isEmpty()) {

                }

                if (HttpHeaderUtil.isTransferEncodingChunked(response)) {

                } else {

                }
            }
            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                ByteBuf buf = content.content();
                byte[] fragment = new byte[buf.readableBytes()];
                buf.readBytes(fragment);

                response = ArrayUtils.addAll(response, fragment);

                onByteReceived.apply(fragment);

                if (content instanceof LastHttpContent) {

                    ctx.close();
                    onConnectionClose.apply(null);
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    };

    public void prepare() throws SSLException {
        scheme = uri.getScheme() == null ? HTTP : uri.getScheme();
        host = uri.getHost() == null ? LOCALHOST : uri.getHost();
        port = uri.getPort();

        if (port == -1) {
            if (HTTP.equalsIgnoreCase(scheme)) {
                port = DEFAULT_HTTP_PORT;
            } else if (HTTPS.equalsIgnoreCase(scheme)) {
                port = DEFAULT_HTTPS_PORT;
            }
        }

        if (!HTTP.equalsIgnoreCase(scheme) && !HTTPS.equalsIgnoreCase(scheme)) {
            throw new IllegalStateException("The scheme must be HTTP");
        }

        // Configure SSL context if necessary.
        final boolean ssl = HTTPS.equalsIgnoreCase(scheme);

        if (ssl) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }


    }

    private void send() {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer(sslCtx, handler));

            // Make the connection attempt.
            Channel ch = bootstrap.connect(host, port).sync().channel();

            // Prepare the HTTP request.
            HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath() + "?" + uri.getRawQuery());
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            // Set some example cookies.
            /*request.headers().set(
                    HttpHeaderNames.COOKIE,
                    io.netty.handler.codec.http.cookie.ClientCookieEncoder.STRICT.encode(
                            new io.netty.handler.codec.http.cookie.DefaultCookie("my-cookie", "foo"),
                            new io.netty.handler.codec.http.cookie.DefaultCookie("another-cookie", "bar")));*/

            // Send the HTTP request.

            ch.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    group.shutdownGracefully();

                }
            });

            ch.writeAndFlush(request);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void execute(final URI requestURI, Function<byte[], ?> onByteReceived, Function<Void, Void> onConnectionClose) {
        response = new byte[0];

        this.onByteReceived = onByteReceived;
        this.onConnectionClose = onConnectionClose;

        this.uri = requestURI;

        try {
            prepare();
        } catch (SSLException e) {
            System.out.println();
            // TODO: do something here
        }
        send();
    }
}
