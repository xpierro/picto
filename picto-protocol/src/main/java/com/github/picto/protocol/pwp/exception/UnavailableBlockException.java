package com.github.picto.protocol.pwp.exception;

/**
 * Created by Pierre on 15/09/15.
 */
public class UnavailableBlockException extends Exception {
    public UnavailableBlockException(String message) {
        super(message);
    }

    public UnavailableBlockException(String message, Throwable cause) {
        super(message, cause);
    }
}
