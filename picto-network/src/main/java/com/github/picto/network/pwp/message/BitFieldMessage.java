package com.github.picto.network.pwp.message;

/**
 * Created by Pierre on 06/09/15.
 */
public class BitFieldMessage extends AbstractMessage implements Message {
    public BitFieldMessage(byte[] bytes) {
        super(bytes);
    }

    @Override
    public MessageType getType() {
        return MessageType.BITFIELD;
    }
}
