package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

import java.util.BitSet;

/**
 * Created by Pierre on 06/09/15.
 */
public class BitFieldMessage extends AbstractMessage implements Message {

    private BitSet bitSet;

    public BitFieldMessage(byte[] payload) throws CannotReadMessageException {
        super(payload);
        readPayload();
    }

    @Override
    public MessageType getType() {
        return MessageType.BITFIELD;
    }

    private void readPayload() {
        bitSet = new BitSet();
        for (int i = 0; i < payload.length * 8; i++) {
            if ((payload[payload.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bitSet.set(i);
            }
        }
    }

    /**
     * Returns the payload of the message as a bitset.
     * @return
     */
    public BitSet getBitSet() {
        return bitSet;
    }
}
