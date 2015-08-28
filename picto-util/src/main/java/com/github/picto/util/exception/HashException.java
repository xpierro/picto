package com.github.picto.util.exception;

/**
 * Created by Pierre on 29/08/15.
 */
public class HashException extends Exception {
    public HashException(String message) {
        super(message);
    }

    public HashException(String message, Throwable cause) {
        super(message, cause);
    }
}
