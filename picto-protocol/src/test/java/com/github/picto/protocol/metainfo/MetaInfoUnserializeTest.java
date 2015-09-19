package com.github.picto.protocol.metainfo;

import com.github.picto.bencode.BEncodeReader;
import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.exception.CannotUnserializeException;
import com.github.picto.bencode.serialization.BEncodeUnserializer;
import com.github.picto.bencode.type.BEncodeableDictionary;
import com.github.picto.protocol.metainfo.model.MetaInfo;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test the unserialization of Meta Info objects from a Bencoded object tree.
 *
 * Created by Pierre on 25/08/15.
 */
public class MetaInfoUnserializeTest {

    @Test
    public void shouldUnserializeTorrentFile() throws CannotReadTokenException, CannotReadBencodedException, CannotUnserializeException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/protocol/metainfo/ubuntu.torrent");

        BEncodeableDictionary dictionary = (BEncodeableDictionary) new BEncodeReader(stream).readBencodable();

        // We have a meta info dictionary, let's transform it into a MetaInfo object
        BEncodeUnserializer<MetaInfo> unserializer = new BEncodeUnserializer<>(dictionary, MetaInfo.class);
        MetaInfo metaInfo = unserializer.unserialize();

        assertNotNull(metaInfo);
        assertEquals("Ubuntu CD releases.ubuntu.com",  metaInfo.getComment());
    }
}
