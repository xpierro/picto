package com.github.picto.network.pwp.message;

/**
 * Created by Pierre on 12/09/15.
 */
public class KeepAliveMessage extends AbstractMessage implements Message {
    @Override
    public MessageType getType() {
        return MessageType.KEEPALIVE;
    }
}
