package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

/**
 * Created by Pierre on 06/09/15.
 */
public class ChokeMessage extends AbstractMessage implements Message {

    public ChokeMessage(byte[] bytes) throws CannotReadMessageException {
        super(bytes, 1);
    }
}
