package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 06/09/15.
 */
public abstract class AbstractMessage implements Message {

    protected byte[] bytes;
    protected MessageType messageType;

    public AbstractMessage(byte[] bytes) {
        this.bytes = bytes;
    }

    public AbstractMessage(byte[] bytes, int expectedLength) throws CannotReadMessageException {
        if (bytes.length != expectedLength) {
            throw new CannotReadMessageException("Impossible to read message of type " + messageType + " as it goes beyong the expected length of " + expectedLength + " (" + bytes.length + ")");
        }
        this.bytes = bytes;
    }

    protected AbstractMessage() {

    }

    @Override
    public byte[] getRawBytes() {
        return bytes;
    }

}
