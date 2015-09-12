package com.github.picto.network.pwp.message;

import java.util.BitSet;

/**
 * Created by Pierre on 06/09/15.
 */
public class BitFieldMessage extends AbstractMessage implements Message {
    public BitFieldMessage(byte[] payload) {
        super(payload);
    }

    @Override
    public MessageType getType() {
        return MessageType.BITFIELD;
    }

    /**
     * Returns the payload of the message as a bitset.
     * @return
     */
    public BitSet getBitSet() {
        BitSet bits = new BitSet();
        for (int i = 0; i < payload.length * 8; i++) {
            if ((payload[payload.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }
}
