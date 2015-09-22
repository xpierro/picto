package com.github.picto.bencode.type;

import com.github.picto.bencode.BEncodeTypeToken;
import com.github.picto.bencode.exception.CannotWriteBencodedException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a dictionary, or map, of key=>values.
 *
 * Created by Pierre on 24/08/15.
 */
public class BEncodeableDictionary implements BEncodeableType {
    private final Map<String, BEncodeableType> map;

    private byte[] bytes;

    public BEncodeableDictionary() {
        map = new HashMap<>();
    }

    public void put(final String key, final BEncodeableType value) {
        map.put(key, value);
    }

    public Optional<BEncodeableType> get(final String key) {
        if (map.containsKey(key)) {
            return Optional.of(map.get(key));
        }
        return Optional.empty();
    }

    @Override
    public void encode(OutputStream output) throws CannotWriteBencodedException {
        try {
            output.write((int) BEncodeTypeToken.DICTIONARY_START.getToken());
            for(Map.Entry<String, BEncodeableType> entry : map.entrySet()) {
                new BEncodeableByteArray(entry.getKey()).encode(output);
                entry.getValue().encode(output);

            }
            output.write((int) BEncodeTypeToken.END.getToken());
        } catch (IOException e) {
            throw new CannotWriteBencodedException("Impossible to write to the output stream.", e);
        }
    }

    public byte[] getBEncodedBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public void setBEncodedBytes(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }
}
