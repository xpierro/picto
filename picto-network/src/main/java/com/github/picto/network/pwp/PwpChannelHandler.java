package com.github.picto.network.pwp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Pierre on 04/09/15.
 */
public class PwpChannelHandler extends SimpleChannelInboundHandler<byte[]> {

    private final PeerWire peerWire;

    public PwpChannelHandler(final PeerWire peerWire) {
        this.peerWire = peerWire;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        // TODO: here do more protocole stuff (emit message only if complete etc
        peerWire.emitMessage(msg);
    }
}
