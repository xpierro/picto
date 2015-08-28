package com.github.picto.httputil.exception;

/**
 * Created by Pierre on 27/08/15.
 */
public class URLEncodeException extends Exception {
    public URLEncodeException(String message) {
        super(message);
    }

    public URLEncodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
