package com.github.picto.network.pwp.message;

/**
 * Created by Pierre on 03/09/15.
 */
public interface Message {

    /**
     * Gets the full ordered bytes of the message.
     */
    byte[] getRawBytes();

    /**
     * Returns the message type.
     * @return
     */
    MessageType getType();

    int getMessageId();

    int getMessageLength();
}
