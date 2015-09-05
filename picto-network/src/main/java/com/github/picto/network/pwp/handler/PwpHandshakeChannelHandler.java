package com.github.picto.network.pwp.handler;

import com.github.picto.network.pwp.PeerWire;
import com.github.picto.network.pwp.message.PwpHandshakeMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the reception of handshare message
 * Created by Pierre on 05/09/15.
 */
public class PwpHandshakeChannelHandler extends SimpleChannelInboundHandler<byte[]> {

    private final PeerWire peerWire;

    public PwpHandshakeChannelHandler(final PeerWire peerWire) {
        super();
        this.peerWire = peerWire;
    }

    /**
     * The only message ever read is a handshake.
     * When the handshake is fully read, the pipeline of the socket channel must be reconfigured to read traditional
     * length-prefixed messages.
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        // The message is framed and should only appear once

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Received a potential handshake of length " + msg.length);
        PwpHandshakeMessage handshakeMessage = new PwpHandshakeMessage(msg);

        ctx.pipeline().addLast(new PwpChannelHandler(peerWire));
        // We remove the handshake handler
        ctx.pipeline().remove(this);

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "New handshake received from " + peerWire);

    }
}
