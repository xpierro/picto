package com.github.picto.bencode.exception;

/**
 * Thrown if a problem appears during unserialization of a BEncodeable object tree.
 * Created by Pierre on 25/08/15.
 */
public class CannotUnserializeException extends Exception {

    public CannotUnserializeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CannotUnserializeException(final String message) {
        super(message);
    }
}
