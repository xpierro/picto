package com.github.picto.network.pwp;

import com.github.picto.network.pwp.handler.PwpChannelInitializer;
import com.github.picto.network.pwp.message.PwpHandshakeMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
public abstract class TcpSender {

    public void sendHandshake(final InetAddress address, final int port, final PwpHandshakeMessage handshakeMessage) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new PwpChannelInitializer() {
            @Override
            public void onNewWire(final PeerWire peerWire) {
                emitPeerWire(peerWire);
            }
        });

        try {
            Channel channel = bootstrap.connect(address, port).sync().channel();
            channel.writeAndFlush(handshakeMessage.getRawBytes()).sync();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Handshaking " + address.getHostName() + " : " + port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void emitPeerWire(final PeerWire peerWire);

}
