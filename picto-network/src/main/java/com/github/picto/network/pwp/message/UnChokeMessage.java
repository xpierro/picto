package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 06/09/15.
 */
public class UnChokeMessage extends AbstractMessage implements Message {

    public UnChokeMessage(byte[] bytes) throws CannotReadMessageException {
        super(bytes, 0);
    }

    public UnChokeMessage() throws CannotReadMessageException {
        super(new byte[0]);
    }

    @Override
    public MessageType getType() {
        return MessageType.UNCHOKE;
    }
}
