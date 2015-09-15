package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;
import com.github.picto.util.ByteArrayUtils;

import java.util.Arrays;

/**
 * Created by Pierre on 06/09/15.
 */
public class RequestMessage extends AbstractMessage implements Message {
    private static final int MESSAGE_LENGTH = 13;

    private int pieceIndex;
    private int blockOffset;
    private int length;

    public RequestMessage(final byte[] bytes) throws CannotReadMessageException {
        super(bytes, MESSAGE_LENGTH);
        readPayload();
    }

    public RequestMessage(final int pieceIndex, final int blockOffset, final int length) {
        this.pieceIndex = pieceIndex;
        this.blockOffset = blockOffset;
        this.length = length;

        buildPayload();
    }

    private void buildPayload() {
        payload = new byte[MESSAGE_LENGTH];
        payload[0] = MessageType.REQUEST.getId();
        System.arraycopy(ByteArrayUtils.integerToByteArray(pieceIndex), 0, payload, 1, 4);
        System.arraycopy(ByteArrayUtils.integerToByteArray(blockOffset), 0, payload, 5, 4);
        System.arraycopy(ByteArrayUtils.integerToByteArray(length), 0, payload, 9, 4);
    }

    private void readPayload() {
        pieceIndex = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 1, 4));
        blockOffset = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 5, 4));
        length = ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(payload, 9, 4));
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
