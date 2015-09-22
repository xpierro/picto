package com.github.picto.network.http.event;

import java.util.Arrays;

/**
 * Created by Pierre on 08/09/15.
 */
public class HttpResponseReceivedEvent {
    private final byte[] bytes;

    public HttpResponseReceivedEvent(final byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
