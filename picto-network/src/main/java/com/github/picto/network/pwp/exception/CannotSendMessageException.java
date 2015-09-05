package com.github.picto.network.pwp.exception;

/**
 * Created by Pierre on 05/09/15.
 */
public class CannotSendMessageException extends Exception {

    public CannotSendMessageException(String message) {
        super(message);
    }

    public CannotSendMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
