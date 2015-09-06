package com.github.picto.network.pwp.message;

import com.github.picto.util.ByteArrayUtils;

/**
 * Created by Pierre on 06/09/15.
 */
public class HaveMessage extends AbstractMessage implements Message {

    private static final int MESSAGE_LENGTH = 5;

    private int pieceIndex;

    public HaveMessage(byte[] bytes) {
        super(bytes);
    }

    public HaveMessage(int pieceIndex) {
        super();

        bytes = new byte[MESSAGE_LENGTH];
        bytes[0] = MessageType.HAVE.getId();
        System.arraycopy(ByteArrayUtils.integerToByteArray(pieceIndex), 0, bytes, 1, 4);

    }


}
