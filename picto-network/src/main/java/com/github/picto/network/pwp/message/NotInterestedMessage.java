package com.github.picto.network.pwp.message;

/**
 * Created by Pierre on 06/09/15.
 */
public class NotInterestedMessage extends AbstractMessage implements Message {
    public NotInterestedMessage(byte[] bytes) {
        super(bytes);
    }

    @Override
    public MessageType getType() {
        return MessageType.NOT_INTERESTED;
    }
}
