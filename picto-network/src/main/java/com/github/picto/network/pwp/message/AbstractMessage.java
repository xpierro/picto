package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;
import com.github.picto.util.ByteArrayUtils;

import java.util.Arrays;

/**
 * Created by Pierre on 06/09/15.
 */
public abstract class AbstractMessage implements Message {

    private static final int SIZE_INFO_LENGTH =  4;

    private static final int ID_INFO_LENGTH = 1;

    protected byte[] payload;

    protected AbstractMessage() {

    }

    protected void buildPayload() {
        throw new IllegalStateException("Cannot build the payload for this message.");
    }

    public AbstractMessage(byte[] payload, int expectedLength) throws CannotReadMessageException {
        if (payload.length != expectedLength) {
            throw new CannotReadMessageException("Impossible to read message of type " + getType() + " as it's different from the expected length of " + expectedLength + " (" + payload.length + ")");
        }
        this.payload = payload;
    }

    public AbstractMessage(byte[] payload) throws CannotReadMessageException {
        this(payload, payload.length);
    }

    @Override
    public byte[] getRawBytes() {
        byte[] message = new byte[SIZE_INFO_LENGTH + ID_INFO_LENGTH + payload.length];
        System.arraycopy(ByteArrayUtils.integerToByteArray(payload.length + ID_INFO_LENGTH), 0, message, 0, SIZE_INFO_LENGTH);
        message[SIZE_INFO_LENGTH] = getType().getId();
        System.arraycopy(payload, 0, message, SIZE_INFO_LENGTH + 1, payload.length);
        return message;
    }

    @Override
    public int getMessageId() {
        return getRawBytes()[SIZE_INFO_LENGTH];
    }

    @Override
    public int getMessageLength() {
        return ByteArrayUtils.byteArrayToInteger(Arrays.copyOfRange(getRawBytes(), 0, SIZE_INFO_LENGTH));
    }
}
