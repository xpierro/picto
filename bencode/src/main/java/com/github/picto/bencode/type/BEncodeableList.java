package com.github.picto.bencode.type;

import com.github.picto.bencode.BEncodeTypeToken;
import com.github.picto.bencode.exception.CannotWriteBencodedException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Represents a list of other BEncodeable objects.
 * Created by Pierre on 24/08/15.
 */
public class BEncodeableList implements BEncodeableType, Iterable<BEncodeableType> {
    private final java.util.List<BEncodeableType> bEncodeableList;

    private final Class<? extends BEncodeableType> elementType;

    public BEncodeableList(final Class<? extends BEncodeableType> elementType) {
        bEncodeableList = new ArrayList<>();
        this.elementType = elementType;
    }

    public void add(final BEncodeableType bEncodeable) {
        bEncodeableList.add(bEncodeable);
    }

    public Optional<BEncodeableType> get(final int index) {
        if (index < bEncodeableList.size()) {
            return Optional.of(bEncodeableList.get(index));
        }
        return Optional.empty();
    }

    public int size() {
        return bEncodeableList.size();
    }

    @Override
    public Iterator<BEncodeableType> iterator() {
        return bEncodeableList.iterator();
    }

    @Override
    public void forEach(Consumer<? super BEncodeableType> action) {
        bEncodeableList.forEach(action);
    }

    @Override
    public Spliterator<BEncodeableType> spliterator() {
        return bEncodeableList.spliterator();
    }

    @Override
    public void encode(OutputStream output) throws CannotWriteBencodedException {
        try {
            output.write((int) BEncodeTypeToken.LIST_START.getToken());
            for (BEncodeableType bEncodeable : bEncodeableList) {
                bEncodeable.encode(output);
            }
            output.write((int) BEncodeTypeToken.END.getToken());
        } catch (IOException e) {
            throw new CannotWriteBencodedException("Impossible to write to the output stream.", e);
        }
    }

    public Class<? extends BEncodeableType> getElementType() {
        return elementType;

    }
}
