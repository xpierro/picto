package com.github.picto.network;

import com.github.picto.network.exemple.HttpSnoopClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.GenericFutureListener;
import org.junit.Test;

import javax.net.ssl.SSLException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test of the RxNetty library
 * Created by Pierre on 30/08/15.
 */
public class RxNettyTest {

    private AtomicBoolean shouldEnd;

    @Test
    public void shouldSendAsynchronousGetRequest() throws MalformedURLException, URISyntaxException, SSLException {
        URI uri = new URI("http://www.google.fr");
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        shouldEnd = new AtomicBoolean(false);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpSnoopClientInitializer(sslCtx, shouldEnd));

            // Make the connection attempt.
            Channel ch = b.connect(host, port).sync().channel();

            // Prepare the HTTP request.
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            // Set some example cookies.
            request.headers().set(
                    HttpHeaderNames.COOKIE,
                    io.netty.handler.codec.http.cookie.ClientCookieEncoder.STRICT.encode(
                            new io.netty.handler.codec.http.cookie.DefaultCookie("my-cookie", "foo"),
                            new io.netty.handler.codec.http.cookie.DefaultCookie("another-cookie", "bar")));

            // Send the HTTP request.


            ch.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("Something received, done: " + future.isDone());

                }
            });
            //ch.closeFuture().await();
            ch.writeAndFlush(request);
            System.out.println("Waiting for response");
            int i = 0;
            while(!shouldEnd.get()) {
                System.out.println("Loop : " + i++);
                Thread.sleep(10);
            }
            System.out.println("No need to wait anymore");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }



    }
}
