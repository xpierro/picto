package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 06/09/15.
 */
public class InterestedMessage extends AbstractMessage implements Message {
    private final static int MESSAGE_LENGTH = 1;

    public InterestedMessage(byte[] bytes) throws CannotReadMessageException {
        super(bytes, 1);
    }

    public InterestedMessage() {
        payload = new byte[MESSAGE_LENGTH];
        payload[0] = MessageType.INTERESTED.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.INTERESTED;
    }
}
