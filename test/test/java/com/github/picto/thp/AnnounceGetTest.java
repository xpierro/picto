package com.github.picto.protocol.thp;

import com.github.picto.bencode.BEncodeReader;
import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.exception.CannotUnserializeException;
import com.github.picto.bencode.serialization.BEncodeUnserializer;
import com.github.picto.bencode.type.BEncodeableDictionary;
import com.github.picto.bencode.type.BEncodeableType;
import com.github.picto.protocol.metainfo.model.MetaInfo;
import com.github.picto.protocol.thp.exception.THPRequestException;
import com.github.picto.protocol.thp.model.ThpAnnounceEvent;
import com.github.picto.protocol.thp.model.peerid.StaticPeerId;
import com.github.picto.protocol.thp.request.AnnounceGet;
import com.github.picto.util.exception.HashException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static junit.framework.TestCase.assertNotNull;

/**
 * Test of the announce request
 * Created by Pierre on 29/08/15.
 */
public class AnnounceGetTest {

    @Test
    public void shouldBuildRequestFromTorrent() throws CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, HashException, THPRequestException, IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/metainfo/ubuntu.torrent");
        BEncodeableDictionary dictionary = (BEncodeableDictionary) new BEncodeReader(stream).readBencodable();

        BEncodeUnserializer<MetaInfo> unserializer = new BEncodeUnserializer<>(dictionary, MetaInfo.class);
        MetaInfo metaInfo = unserializer.unserialize();

        URI uri = new AnnounceGet()
                .metaInfo(metaInfo)
                .announceURI(metaInfo.getAnnounce())
                .port(6082)
                .compact(true)
                .peerId(StaticPeerId.DEFAULT)
                .event(ThpAnnounceEvent.STARTED)
                .build()
                .getUri();

        assertNotNull(uri);

        // We connect to the tracker and ask for a peer list
        BEncodeableDictionary response = (BEncodeableDictionary) HttpClientBuilder.create().build().execute(new HttpGet(uri), new ResponseHandler<BEncodeableType>() {
            @Override
            public BEncodeableType handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

                BEncodeReader bEncodeReader = new BEncodeReader(response.getEntity().getContent());
                try {
                    return bEncodeReader.readBencodable();
                } catch (CannotReadBencodedException e) {
                    e.printStackTrace();
                } catch (CannotReadTokenException e) {
                    e.printStackTrace();
                }
                return null;

            }
        });
        assertNotNull(response);
        assertNotNull(response.get("peers"));

    }
}
