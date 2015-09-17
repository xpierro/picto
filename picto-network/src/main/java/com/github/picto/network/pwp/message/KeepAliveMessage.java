package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 12/09/15.
 */
public class KeepAliveMessage extends AbstractMessage implements Message {

    public KeepAliveMessage(byte[] payload) throws CannotReadMessageException {
        super(payload, 0);
    }

    @Override
    public MessageType getType() {
        return MessageType.KEEPALIVE;
    }
}
