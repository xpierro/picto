package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;
import com.github.picto.util.ByteArrayUtils;

import java.util.Arrays;

/**
 * Created by Pierre on 06/09/15.
 */
public class HaveMessage extends AbstractMessage implements Message {

    private static final int MESSAGE_LENGTH = 4;

    private int pieceIndex;

    public HaveMessage(byte[] payload) throws CannotReadMessageException {
        super(payload, MESSAGE_LENGTH);
        pieceIndex = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 0, MESSAGE_LENGTH));
    }



    public HaveMessage(int pieceIndex) {
        super();

        this.pieceIndex = pieceIndex;
        buildPayload();
    }

    protected void buildPayload() {
        payload = new byte[MESSAGE_LENGTH];
        System.arraycopy(ByteArrayUtils.integerToByteArray(pieceIndex), 0, payload, 0, MESSAGE_LENGTH);
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    @Override
    public MessageType getType() {
        return MessageType.HAVE;
    }
}
