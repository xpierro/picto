package com.github.picto.bencode.exception;

/**
 * Thrown when a token cannot be read.
 * Created by Pierre on 24/08/15.
 */
public class CannotReadTokenException extends Exception {

    public CannotReadTokenException(final String message, final Exception cause) {
        super(message, cause);
    }

    public CannotReadTokenException(final String message) {
        super(message);
    }
}
