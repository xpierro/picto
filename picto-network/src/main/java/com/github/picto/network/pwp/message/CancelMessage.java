package com.github.picto.network.pwp.message;

/**
 * Created by Pierre on 06/09/15.
 */
public class CancelMessage extends AbstractMessage implements Message {
    public CancelMessage(byte[] bytes) {
        super(bytes);
    }
}
