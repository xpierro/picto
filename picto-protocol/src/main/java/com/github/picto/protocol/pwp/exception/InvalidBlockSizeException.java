package com.github.picto.protocol.pwp.exception;

/**
 * Created by Pierre on 15/09/15.
 */
public class InvalidBlockSizeException extends Exception {
    public InvalidBlockSizeException(String message) {
        super(message);
    }

    public InvalidBlockSizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
