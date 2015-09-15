package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * TODO: maybe segment the message between payload and id to simplify concatenation everywhere.
 * Created by Pierre on 06/09/15.
 */
public abstract class AbstractMessage implements Message {

    protected byte[] payload;

    public AbstractMessage(byte[] payload) {
        this.payload = payload;
    }

    public AbstractMessage(byte[] payload, int expectedLength) throws CannotReadMessageException {
        if (payload.length != expectedLength) {
            throw new CannotReadMessageException("Impossible to read message of type " + getType() + " as it's different from the expected length of " + expectedLength + " (" + payload.length + ")");
        }
        this.payload = payload;
    }

    protected AbstractMessage() {

    }

    @Override
    public byte[] getRawBytes() {
        return payload;
    }

}
