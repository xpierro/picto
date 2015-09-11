package com.github.picto.network.pwp;

import com.github.picto.network.pwp.handler.PwpChannelInitializer;
import com.google.inject.Inject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client mode sending handshakes before peer wire creation. Opposed to the listener, waiting for a remote client
 * to create a peer wire
 *
 * Created by Pierre on 06/09/15.
 */
public class TcpConnecter {

    @Inject
    private PwpChannelInitializer channelInitializer;

    public void connect(final InetAddress address, final int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        // Will post a peerwire to the event bus once created.
        bootstrap.handler(channelInitializer);

        bootstrap.connect(address, port);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connecting to " + address.getHostName() + " : " + port);

    }
}
