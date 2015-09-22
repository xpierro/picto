package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;
import com.github.picto.util.ByteArrayUtils;

import java.util.Arrays;

/**
 * Created by Pierre on 06/09/15.
 */
public class PieceMessage extends AbstractMessage implements Message {

    private int pieceIndex;
    private int byteOffset;

    private byte[] block;

    public PieceMessage(byte[] payload) throws CannotReadMessageException {
        super(payload);
        readPayload();
    }

    @Override
    public MessageType getType() {
        return MessageType.PIECE;
    }

    public void readPayload() {
        pieceIndex = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 0, 4));
        byteOffset = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 4, 8));

        block = Arrays.copyOfRange(payload, 8, payload.length);
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    public int getByteOffset() {
        return byteOffset;
    }

    public byte[] getBlock() {
        return block;
    }
}
