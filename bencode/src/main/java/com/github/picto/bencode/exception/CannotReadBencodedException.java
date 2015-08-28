package com.github.picto.bencode.exception;

/**
 * Exception thrown when a bencoded string cannot be read.
 * Created by Pierre on 24/08/15.
 */
public class CannotReadBencodedException extends Exception {

    public CannotReadBencodedException(String message, Exception cause) {
        super(message, cause);
    }

    public CannotReadBencodedException(String message) {
        super(message);
    }
}
