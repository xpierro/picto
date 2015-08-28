package com.github.picto.bencode.type;

import com.github.picto.bencode.exception.CannotWriteBencodedException;

import java.io.OutputStream;

/**
 * Represents an Object that can be serialized into a bencoded output stream.
 * Created by Pierre on 24/08/15.
 */
public interface BEncodeableType {

    /**
     * Encodes the BEncodeable object to a bencoded stream.
     * @param output The target output stream
     */
    void encode(final OutputStream output) throws CannotWriteBencodedException;

}
