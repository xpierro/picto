package com.github.picto.network.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Pierre on 03/09/15.
 */
public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    final SimpleChannelInboundHandler<HttpObject> handler;

    public HttpClientInitializer(SslContext sslCtx, final SimpleChannelInboundHandler<HttpObject> handler) {
        this.sslCtx = sslCtx;
        this.handler = handler;
    }

    @Override
    public void initChannel(final SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        // Enable HTTPS if necessary.
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

        p.addLast(new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        p.addLast(new HttpContentDecompressor());

        p.addLast(handler);
    }
}
