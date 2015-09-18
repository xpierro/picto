package com.github.picto.protocol.pwp.exception;

/**
 * Created by Pierre on 15/09/15.
 */
public class BlockAlreadyDownloadedException extends Exception {
    public BlockAlreadyDownloadedException(String message) {
        super(message);
    }

    public BlockAlreadyDownloadedException(String message, Throwable cause) {
        super(message, cause);
    }
}
