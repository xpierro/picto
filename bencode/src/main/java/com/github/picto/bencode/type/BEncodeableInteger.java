package com.github.picto.bencode.type;

import com.github.picto.bencode.BEncodeTypeToken;
import com.github.picto.bencode.exception.CannotWriteBencodedException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a bencoded integer.
 * Created by Pierre on 24/08/15.
 */
public class BEncodeableInteger implements BEncodeableType {
    private final long integer;

    public BEncodeableInteger(long integer) {
        this.integer = integer;
    }

    public long getInteger() {
        return integer;
    }

    @Override
    public void encode(OutputStream output) throws CannotWriteBencodedException {
        try {
            output.write((int) BEncodeTypeToken.INTEGER_START.getToken());
            output.write(("" + integer).getBytes("ASCII"));
            output.write((int) BEncodeTypeToken.END.getToken());
        } catch (IOException e) {
            throw new CannotWriteBencodedException("Impossible to write to the output stream.", e);
        }
    }
}
