package com.github.picto.bencode.exception;

/**
 * Thrown when a writer cannot serialize to a bencoded output.
 * Created by Pierre on 25/08/15.
 */
public class CannotWriteBencodedException extends Exception {
    public CannotWriteBencodedException(final String message, final Exception cause) {
        super(message, cause);
    }
}
