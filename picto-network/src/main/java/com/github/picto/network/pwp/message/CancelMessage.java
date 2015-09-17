package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 06/09/15.
 */
public class CancelMessage extends AbstractMessage implements Message {
    public CancelMessage(byte[] payload) throws CannotReadMessageException {
        super(payload);
    }

    @Override
    public MessageType getType() {
        return MessageType.CANCEL;
    }
}
