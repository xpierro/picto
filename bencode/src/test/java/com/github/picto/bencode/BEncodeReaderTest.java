package com.github.picto.bencode;

import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.type.BEncodeableByteArray;
import com.github.picto.bencode.type.BEncodeableDictionary;
import com.github.picto.bencode.type.BEncodeableInteger;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test of the BEncodeReader class.
 * Created by Pierre on 24/08/15.
 */
public class BEncodeReaderTest {
    @Test
    public void shouldReadBencodedString() throws CannotReadBencodedException, UnsupportedEncodingException, CannotReadTokenException {
        String content = "this is a long test";
        String fullContent = content.length() + ":" + content;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fullContent.getBytes(Charset.forName("UTF-8")));
        BEncodeReader reader = new BEncodeReader(byteArrayInputStream);

        String contentRead = new String(((BEncodeableByteArray) reader.readBencodable()).getBytes(), "UTF-8");

        assertEquals(content, contentRead);
    }

    @Test
    public void shouldReadBencodedStringWithAccent() throws CannotReadBencodedException, UnsupportedEncodingException, CannotReadTokenException {
        String content = "this is a long test with a gâteau !";
        String fullContent = content.getBytes(Charset.forName("UTF-8")).length + ":" + content;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fullContent.getBytes(Charset.forName("UTF-8")));
        BEncodeReader reader = new BEncodeReader(byteArrayInputStream);

        String contentRead = new String(((BEncodeableByteArray)reader.readBencodable()).getBytes(), "UTF-8");

        assertEquals(content, contentRead);
    }

    @Test
    public void shouldReadEmptyString() throws CannotReadBencodedException, UnsupportedEncodingException, CannotReadTokenException {
        String content = "";
        String fullContent = content.getBytes(Charset.forName("UTF-8")).length + ":" + content;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fullContent.getBytes(Charset.forName("UTF-8")));
        BEncodeReader reader = new BEncodeReader(byteArrayInputStream);

        String contentRead = new String(((BEncodeableByteArray)reader.readBencodable()).getBytes(), "UTF-8");

        assertEquals(content, contentRead);
    }

    @Test
    public void shouldReadBencodedInteger() throws CannotReadBencodedException, CannotReadTokenException {
        int contentInt = 3239;
        String fullContent = "i" + contentInt + "e";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fullContent.getBytes(Charset.forName("UTF-8")));
        assertEquals(contentInt, ((BEncodeableInteger) new BEncodeReader(byteArrayInputStream).readBencodable()).getInteger());
    }

    @Test
    public void shouldReadNegativeInteger() throws CannotReadTokenException, CannotReadBencodedException {
        int contentInt = -345329;
        String fullContent = "i" + contentInt + "e";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fullContent.getBytes(Charset.forName("UTF-8")));
        assertEquals(contentInt, ((BEncodeableInteger) new BEncodeReader(byteArrayInputStream).readBencodable()).getInteger());
    }

    @Test
    public void shouldReadTorrentFile() throws CannotReadTokenException, CannotReadBencodedException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/metainfo/ubuntu.torrent");
        BEncodeableDictionary dictionary = (BEncodeableDictionary) new BEncodeReader(stream).readBencodable();
        assertNotNull(dictionary);
        assertEquals("Ubuntu CD releases.ubuntu.com", ((BEncodeableByteArray) dictionary.get("comment").get()).toUtf8String().get());

    }
}
