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
import java.util.concurrent.atomic.AtomicBoolean;

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

    private AtomicBoolean shouldEnd = new AtomicBoolean(false);

    @Test
    public void shouldDoGetRequestWithExecutor() throws HashException, THPRequestException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, InterruptedException {
        /*InputStream stream = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/metainfo/ubuntu.torrent");
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

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        shouldEnd.set(false);

        GetExecutor getExecutor = new GetExecutor();
        getExecutor.execute(uri, new Function<byte[], Object>() {
            @Override
            public Object apply(byte[] bytes) {
                try {
                    byteArrayOutputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                BEncodeReader bEncodeReader = new BEncodeReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                BEncodeableType type = null;
                TrackerAnnounceResponseModel responseModel = null;
                try {
                    type = bEncodeReader.readBencodable();
                    responseModel = new BEncodeUnserializer<>((BEncodeableDictionary) type, TrackerAnnounceResponseModel.class).unserialize();
                } catch (CannotReadBencodedException e) {
                    e.printStackTrace();
                } catch (CannotReadTokenException e) {
                    e.printStackTrace();
                } catch (CannotUnserializeException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Assert.assertNotNull(responseModel);
                shouldEnd.set(true);

                return null;
            }
        });

        int i = 0;
        while (!shouldEnd.get()) {
            System.out.println("Loop : " + i++);
            Thread.sleep(10);
        }*/
    }

    @Test
    public void shouldDoGetRequestWithPeerWire() throws HashException, THPRequestException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, InterruptedException {

        /*final TrackerAnnounceResponseModel[] responseModel = {null};

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

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        shouldEnd.set(false);

        GetExecutor getExecutor = new GetExecutor();
        getExecutor.execute(uri, new Function<byte[], Object>() {
            @Override
            public Object apply(byte[] bytes) {
                try {
                    byteArrayOutputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {
                BEncodeReader bEncodeReader = new BEncodeReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                BEncodeableType type = null;

                try {
                    type = bEncodeReader.readBencodable();
                    responseModel[0] = new BEncodeUnserializer<>((BEncodeableDictionary) type, TrackerAnnounceResponseModel.class).unserialize();
                } catch (CannotReadBencodedException e) {
                    e.printStackTrace();
                } catch (CannotReadTokenException e) {
                    e.printStackTrace();
                } catch (CannotUnserializeException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Assert.assertNotNull(responseModel[0]);
                shouldEnd.set(true);

                return null;
            }
        });

        int i = 0;
        while (!shouldEnd.get()) {

            Thread.sleep(100);
        }

        // We now create a peer wire
        Peer peer = responseModel[0].getPeers().get(0);

        TcpSender tcpSender = new TcpSender() {
            @Override
            public void emitPeerWire(PeerWire peerWire) {
                System.out.println();
                peerWire.addObserver(new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        System.out.println();
                    }
                });
            }
        };

        tcpSender.sendHandshake(peer.getHost(), peer.getPort(), new PwpHandshakeMessage().peerId(StaticPeerId.DEFAULT.getPeerIdBytes()).infoHash(Hasher.sha1(metaInfo.getInformation().getBEncodeableDictionary().getBEncodedBytes())));

        shouldEnd.set(false);

        while (!shouldEnd.get()) {

            Thread.sleep(100);
        }*/
    }
}
