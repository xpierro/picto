package com.github.picto.network.pwp;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Observable;

/**
 * Peer connection, allowing to receive and send message to a peer.
 *
 * Created by Pierre on 03/09/15.
 */
public class PeerWire extends Observable {
    private SocketChannel socketChannel;

    public PeerWire(final SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public PeerWire(final InetAddress address, final int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                socketChannel = ch;
                ch.pipeline().addLast(new PwpChannelHandler(PeerWire.this));
            }
        });

        ChannelFuture f = b.connect(address, port);
    }

    public void emitMessage(final Object message) {

        this.notifyObservers(message);
    }

    public void sendMessage(final Message message) {
        byte[] bytes = message.getRawBytes();
        ByteBuffer buf = ByteBuffer.allocate(bytes.length);
        buf.clear();
        buf.put(bytes);
        socketChannel.writeAndFlush(buf);
    }

}
