package com.github.picto.network.pwp.handler;

import com.github.picto.network.pwp.PeerWire;
import com.github.picto.network.pwp.event.NewPeerWireEvent;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * Initializes a socket channel.
 * Created by Pierre on 05/09/15.
 */
public class PwpChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Inject
    private EventBus eventBus;

    public PwpChannelInitializer() {

    }

    /**
     * The lifecycle is as follow: first message ever received or transmitted is a handshake. It will come from both side
     * The handle must therefore be specialized.
     * @param ch The socket chanel connected to the remote peer.
     * @throws Exception
     */
    @Override
    public void initChannel(final SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        PeerWire peerWire = new PeerWire(ch);

        pipeline.addLast("handshakeDecoder", new PwpHandshakeDecoder() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);

                // The new peerwire can only be sent to the rest of the application after the socket has been connected
                // successfully
                eventBus.post(new NewPeerWireEvent(peerWire));
            }
        });

        pipeline.addLast(new ByteArrayDecoder());

        pipeline.addLast(new PwpChannelHandler(peerWire));
        pipeline.addLast(new ByteArrayEncoder());

    }
}
