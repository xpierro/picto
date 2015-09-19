package com.github.picto.protocol.thp;

import com.github.picto.bencode.BEncodeReader;
import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.exception.CannotUnserializeException;
import com.github.picto.bencode.serialization.BEncodeUnserializer;
import com.github.picto.bencode.type.BEncodeableDictionary;
import com.github.picto.network.http.exception.URLEncodeException;
import com.github.picto.network.http.RequestBuilder;
import com.github.picto.protocol.metainfo.model.MetaInfo;
import com.github.picto.protocol.thp.model.AnnounceRequestModel;
import com.github.picto.util.ByteArrayUtils;
import junit.framework.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Pierre on 28/08/15.
 */
public class AnnounceRequestTest {

    @Test
    public void shouldBuildRequestURIWithInfoHash() throws URISyntaxException, URLEncodeException {
        AnnounceRequestModel announceRequest = new AnnounceRequestModel();

        byte[] infoHash = ByteArrayUtils.hexStringToByteArray("123456789abc");
        announceRequest.setInfoHash(infoHash);

        RequestBuilder requestBuilder = new RequestBuilder();
        URI uri = requestBuilder.buildGetUri(announceRequest, "test.com:80");

        assertNotNull(uri);

    }

    @Test
    public void shouldBuildRequestFromMetaInfo() throws CannotReadTokenException, CannotReadBencodedException, CannotUnserializeException, URISyntaxException, URLEncodeException, NoSuchAlgorithmException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/protocol/metainfo/ubuntu.torrent");
        BEncodeableDictionary dictionary = (BEncodeableDictionary) new BEncodeReader(stream).readBencodable();

        BEncodeUnserializer<MetaInfo> unserializer = new BEncodeUnserializer<>(dictionary, MetaInfo.class);
        MetaInfo metaInfo = unserializer.unserialize();

        AnnounceRequestModel request = new AnnounceRequestModel();
        request.setInfoHash(MessageDigest.getInstance("SHA-1").digest(metaInfo.getInformation().getBEncodeableDictionary().getBEncodedBytes()));

        RequestBuilder requestBuilder = new RequestBuilder();
        URI uri = requestBuilder.buildGetUri(request, metaInfo.getAnnounce());
        Assert.assertNotNull(uri);
        assertEquals("fc8a15a2faf2734dbb1dc5f7afdc5c9beaeb1f59".toUpperCase(), ByteArrayUtils.bytesToHex(request.getInfoHash()));

    }


}
