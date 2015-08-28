package com.github.picto.bencode;

import com.github.picto.bencode.exception.CannotWriteBencodedException;
import com.github.picto.bencode.type.BEncodeableType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Writes to a bencoded output stream.
 * Created by Pierre on 25/08/15.
 */
public class BEncodeWriter extends OutputStreamWriter {
    private final OutputStream outputStream;

    public BEncodeWriter(final OutputStream outputStream) {

        super(outputStream);
        this.outputStream = outputStream;
    }

    public void write(final BEncodeableType bEncodeable) throws CannotWriteBencodedException {
        bEncodeable.encode(outputStream);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException { }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
