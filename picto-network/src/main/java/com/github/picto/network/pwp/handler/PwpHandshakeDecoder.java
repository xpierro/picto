package com.github.picto.network.pwp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * Created by Pierre on 06/09/15.
 */
public class PwpHandshakeDecoder extends FixedLengthFrameDecoder {
    private static final int HANDSHAKE_LENGTH = 68;

    /**
     * Creates a new instance.
     *
     * @param frameLength the length of the frame
     */
    public PwpHandshakeDecoder() {
        super(HANDSHAKE_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object handshake = super.decode(ctx, in);

        ctx.pipeline().addFirst(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, Integer.MAX_VALUE, 0, 4, 0, 4, true));
        if (in.isReadable()) {
            Object[] response = new Object[] { in.readBytes(in.readableBytes())};
            ctx.pipeline().remove(this);
            return response;
        } else {
            ctx.pipeline().remove(this);
            return handshake;
        }

    }

}
