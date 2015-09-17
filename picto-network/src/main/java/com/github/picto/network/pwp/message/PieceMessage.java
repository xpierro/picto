package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 06/09/15.
 */
public class PieceMessage extends AbstractMessage implements Message {

    public PieceMessage(byte[] payload) throws CannotReadMessageException {
        super(payload);
    }

    @Override
    public MessageType getType() {
        return MessageType.PIECE;
    }
}
