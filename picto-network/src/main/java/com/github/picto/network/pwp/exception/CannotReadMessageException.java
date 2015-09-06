package com.github.picto.network.pwp.exception;

/**
 * Created by Pierre on 06/09/15.
 */
public class CannotReadMessageException extends Exception {

    public CannotReadMessageException(String message) {
        super(message);
    }

    public CannotReadMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
