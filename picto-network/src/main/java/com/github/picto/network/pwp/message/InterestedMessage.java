package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 06/09/15.
 */
public class InterestedMessage extends AbstractMessage implements Message {

    public InterestedMessage(byte[] payload) throws CannotReadMessageException {
        super(payload, 0);
    }

    public InterestedMessage() throws CannotReadMessageException {

        super(new byte[0]);
    }

    @Override
    public MessageType getType() {
        return MessageType.INTERESTED;
    }
}
