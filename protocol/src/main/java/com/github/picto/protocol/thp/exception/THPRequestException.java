package com.github.picto.protocol.thp.exception;

/**
 * Created by Pierre on 29/08/15.
 */
public class THPRequestException extends Exception {
    public THPRequestException(String message) {
        super(message);
    }

    public THPRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
