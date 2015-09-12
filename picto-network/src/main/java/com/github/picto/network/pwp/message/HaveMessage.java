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
        pieceIndex = ByteArrayUtils.byteArrayToInteger(bytes);
    }

    public HaveMessage(int pieceIndex) {
        super();

        payload = new byte[MESSAGE_LENGTH];
        payload[0] = MessageType.HAVE.getId();
        System.arraycopy(ByteArrayUtils.integerToByteArray(pieceIndex), 0, payload, 1, 4);
        this.pieceIndex = pieceIndex;

    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    @Override
    public MessageType getType() {
        return MessageType.HAVE;
    }
}
