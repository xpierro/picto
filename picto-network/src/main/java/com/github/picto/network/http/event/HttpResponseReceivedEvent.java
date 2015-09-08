package com.github.picto.network.http.event;

/**
 * Created by Pierre on 08/09/15.
 */
public class HttpResponseReceivedEvent {
    private final byte[] bytes;

    public HttpResponseReceivedEvent(final byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
