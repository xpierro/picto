package com.github.picto.network.pwp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Peer Wire Protocol listener, listening on a port for new client request.
 * Emits a PeerConnection when a connection is received.
 * We know that the messages are length-prefixed, the first four bytes being a 32 bytes integer in network byte order.
 * TODO: this is to be done on another level.
 * Created by Pierre on 02/09/15.
 */
public abstract class TcpListener {

    private final int port;
    private ServerBootstrap serverBootstrap;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ChannelFuture channelFuture;

    public TcpListener(final int port) throws InterruptedException {
        this.port = port;

        prepare();
    }

    private void prepare() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();

                        final PeerWire peerWire = new PeerWire(ch);
                        emitWire(peerWire);

                        p.addLast(new PwpChannelHandler(peerWire));

                    }

                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);


        bind();


    }

    private void bind() throws InterruptedException {
        channelFuture = serverBootstrap.bind(port).sync();
    }

    private void close() throws InterruptedException {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        channelFuture.channel().closeFuture().sync();
    }

    /**
     * Called when a new connection arrives
     * @param peerWire
     */
    protected abstract void emitWire(final PeerWire peerWire);


}