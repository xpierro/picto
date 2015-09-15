package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 06/09/15.
 */
public class UnChokeMessage extends AbstractMessage implements Message {
    private static final int MESSAGE_LENGTH = 1;

    public UnChokeMessage(byte[] bytes) throws CannotReadMessageException {
        super(bytes, 1);
    }

    public UnChokeMessage() {
        payload = new byte[MESSAGE_LENGTH];
        payload[0] = MessageType.UNCHOKE.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.UNCHOKE;
    }
}
