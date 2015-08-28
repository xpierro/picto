package com.github.picto.bencode;

import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.exception.CannotWriteBencodedException;
import com.github.picto.bencode.type.BEncodeableDictionary;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test the BEncodeWriter class.
 * Created by Pierre on 25/08/15.
 */
public class BEncodeWriterTest {
    @Test
    public void shouldProduceCompatibleTorrentFile() throws CannotReadTokenException, CannotReadBencodedException, IOException, CannotWriteBencodedException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/metainfo/ubuntu.torrent");
        BEncodeableDictionary dictionary = (BEncodeableDictionary) new BEncodeReader(stream).readBencodable();

        // We now write the expected result to a byte array

        InputStream input = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/metainfo/ubuntu.torrent");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        byte[] expected = baos.toByteArray();

        baos = new ByteArrayOutputStream();
        new BEncodeWriter(baos).write(dictionary);

        byte[] actual = baos.toByteArray();

        // Since our writer can order the content differently we will count the number of each byte code for each file.

        Map<Byte, Integer> countExpected = countBytes(expected);
        Map<Byte, Integer> countActual = countBytes(actual);

        for (byte b : expected) {
            assertEquals(countExpected.get(b), countActual.get(b));
        }

        assertEquals(expected.length, actual.length);

    }

    private Map<Byte, Integer> countBytes(final byte[] bytes) {
        Map<Byte, Integer> count = new HashMap<>();

        for (byte b : bytes) {
            if (!count.containsKey(b)) {
                count.put(b, 0);
            }
            count.put(b, count.get(b) + 1);
        }

        return count;
    }
}
