package com.github.picto.network.pwp.handler;

import com.github.picto.network.pwp.PeerWire;
import com.github.picto.network.pwp.message.MessageFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        if (msg.length == 0) {
            //TODO: should we handle that on a higher level ?
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "A keep-alive message has been received");
        } else {
            peerWire.onMessageReceived(MessageFactory.getMessage(msg));
        }
    }
}
