package com.github.picto.bencode.type;

import com.github.picto.bencode.exception.CannotWriteBencodedException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Reprensents an array of bytes of any meaning
 * Created by Pierre on 24/08/15.
 */
public class BEncodeableByteArray implements BEncodeableType {
    private final static char SEPARATOR = ':';
    private final byte[] bytes;

    public BEncodeableByteArray(final byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public BEncodeableByteArray(String utf8String) throws UnsupportedEncodingException {
        this.bytes = utf8String.getBytes("UTF-8");
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public Optional<String> toUtf8String() {
        try {
            return Optional.of(new String(bytes, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return Optional.empty();
        }
    }

    @Override
    public void encode(OutputStream output) throws CannotWriteBencodedException {
        try {
            output.write(("" + bytes.length).getBytes("ASCII"));
            output.write((int) SEPARATOR);
            output.write(bytes);
        } catch (IOException e) {
            throw new CannotWriteBencodedException("Impossible to write to the output stream.", e);
        }
    }
}
