package com.github.picto.network.pwp.message;

/**
 * Created by Pierre on 06/09/15.
 */
public class PieceMessage extends AbstractMessage implements Message {
    public PieceMessage(byte[] bytes) {
        super(bytes);
    }

    @Override
    public MessageType getType() {
        return MessageType.PIECE;
    }
}
