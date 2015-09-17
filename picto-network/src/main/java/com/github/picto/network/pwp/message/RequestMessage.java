package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;
import com.github.picto.util.ByteArrayUtils;

import java.util.Arrays;

/**
 * Created by Pierre on 06/09/15.
 */
public class RequestMessage extends AbstractMessage implements Message {
    private static final int MESSAGE_LENGTH = 12;

    private int pieceIndex;
    private int blockOffset;
    private int length;

    public RequestMessage(final byte[] payload) throws CannotReadMessageException {
        super(payload);
        readPayload();
    }

    public RequestMessage(final int pieceIndex, final int blockOffset, final int length) {
        super();
        this.pieceIndex = pieceIndex;
        this.blockOffset = blockOffset;
        this.length = length;
        buildPayload();
    }

    protected void buildPayload() {
        payload = new byte[MESSAGE_LENGTH];
        System.arraycopy(ByteArrayUtils.integerToByteArray(pieceIndex), 0, payload, 0, 4);
        System.arraycopy(ByteArrayUtils.integerToByteArray(blockOffset), 0, payload, 4, 4);
        System.arraycopy(ByteArrayUtils.integerToByteArray(length), 0, payload, 8, 4);
    }

    private void readPayload() {
        pieceIndex = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 0, 4));
        blockOffset = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 4, 8));
        length = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 8, 12));
    }

    @Override
    public MessageType getType() {
        return MessageType.REQUEST;
    }

    /**
     * Returns the 0-based index of the piece.
     */
    public int getPieceIndex() {
        return pieceIndex;
    }

    /**
     * Returns the 0-based index of the block within the piece
     * @return
     */
    public int getBlockOffset() {
        return blockOffset;
    }

    /**
     * Returns the length of the requested block.
     */
    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return String.format("[pieceIndex: %s, blockOffset: %s, length: %s]", pieceIndex, blockOffset, length);
    }
}
