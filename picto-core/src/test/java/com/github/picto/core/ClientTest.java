package com.github.picto.core;

import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.exception.CannotUnserializeException;
import com.github.picto.core.client.Client;
import com.github.picto.core.client.ClientSettings;
import com.github.picto.module.FilesystemModule;
import com.github.picto.module.ProtocolModule;
import com.github.picto.network.pwp.exception.CannotReadMessageException;
import com.github.picto.network.pwp.message.*;
import com.github.picto.protocol.event.MetaInfoLoadedEvent;
import com.github.picto.protocol.event.NewConnectedPeerEvent;
import com.github.picto.protocol.event.PeerListChangedEvent;
import com.github.picto.protocol.event.PeerMessageReceivedEvent;
import com.github.picto.protocol.metainfo.model.MetaInfo;
import com.github.picto.protocol.pwp.model.Peer;
import com.github.picto.protocol.thp.exception.THPRequestException;
import com.github.picto.util.exception.HashException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by Pierre on 08/09/15.
 */
public class ClientTest {

    private Client client;
    private EventBus eventBus;

    /** Countdown latch */
    private CountDownLatch lock;

    private InputStream torrentStream;

    private MetaInfo metaInfo;

    private Collection<Peer> peers;

    private void initGuice() {
        Injector injector = Guice.createInjector(new ProtocolModule(), new FilesystemModule());
        client = injector.getInstance(Client.class);
        eventBus = injector.getInstance(EventBus.class);
    }

    private void initEventBus() {
        eventBus.register(this);
    }

    private void initTorrentStream() {
        torrentStream = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/core/metainfo/ubuntu.torrent");
        assertNotNull(torrentStream);
    }

    private void initClient() {
        client.setListenPort(6669);
    }

    @Subscribe
    public void metaInfoLoaded(MetaInfoLoadedEvent event) {
        System.out.println("Meta Info Loaded");
        assertNull(metaInfo);
        metaInfo = client.getMetaInfo();
        assertNotNull(metaInfo);
        lock.countDown();
    }

    @Subscribe
    public void peerListRefreshed(PeerListChangedEvent event) {
        System.out.println("Peer list refresh");
        assertNull(peers);
        peers = client.getPeers();
        assertNotNull(peers);
        lock.countDown();
    }

    @Subscribe
    public void newConnectedPeer(NewConnectedPeerEvent event) {
        System.out.println("New connected peer received");
        lock.countDown();
    }

    //@Test
    public void clientShouldLoadMetaInfo() throws CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, InterruptedException {
        lock = new CountDownLatch(1);
        initGuice();
        initEventBus();
        initTorrentStream();

        client.loadMetaInfo(torrentStream);
        System.out.println("Async");
        lock.await(2, TimeUnit.SECONDS);
        assertNotNull(metaInfo);
    }

    //@Test
    public void clientShouldLoadPeerList() throws CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, THPRequestException, HashException, InterruptedException {
        lock = new CountDownLatch(2);

        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.loadMetaInfo(torrentStream);
        client.refreshPeerList();

        System.out.println("Async");
        lock.await(10, TimeUnit.SECONDS);
        assertNotNull(peers);
        assertFalse(peers.isEmpty());

        System.out.println("The client returned " + peers.size() + " peers : " + peers);

    }

    //@Test
    public void clientShouldConnectToPeer() throws CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, THPRequestException, HashException, InterruptedException {
        lock = new CountDownLatch(2);

        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.loadMetaInfo(torrentStream);
        client.refreshPeerList();

        System.out.println("Refreshing peer list");
        lock.await(30, TimeUnit.SECONDS);
        assertNotNull(peers);
        assertFalse(peers.isEmpty());

        lock = new CountDownLatch(1);
        // We take the first peer of the list
        Peer testedPeer = peers.iterator().next();
        client.connectToPeer(testedPeer);
        lock.await(30, TimeUnit.SECONDS);

        assertNotNull(testedPeer.getPeerWire());
        System.out.println("A new peerwire has been created for " + peers);
    }

    //@Test
    public void peerShouldTransmitEvent() throws InterruptedException, CannotReadMessageException, THPRequestException, HashException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException {
        lock = new CountDownLatch(2);
        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.loadMetaInfo(torrentStream);
        client.refreshPeerList();

        System.out.println("Refreshing peer list");
        lock.await(30, TimeUnit.SECONDS);
        assertNotNull(peers);
        assertFalse(peers.isEmpty());

        Peer testedPeer = peers.iterator().next();

        testMessageType(testedPeer, MessageType.CHOKE, new ChokeMessage(new byte[1]));
        assertTrue(testedPeer.isChokingUs());

        testMessageType(testedPeer, MessageType.UNCHOKE, new UnChokeMessage(new byte[1]));
        assertFalse(testedPeer.isChokingUs());

        testMessageType(testedPeer, MessageType.INTERESTED, new InterestedMessage(new byte[1]));
        assertTrue(testedPeer.isInterestedInUs());

        testMessageType(testedPeer, MessageType.NOT_INTERESTED, new NotInterestedMessage(new byte[1]));
        assertFalse(testedPeer.isInterestedInUs());
    }

    private void testMessageType(final Peer testedPeer, final MessageType messageType, Message message) throws InterruptedException {
        expectedMessageType = messageType;
        expectedMessageReceived = false;
        lock = new CountDownLatch(1);
        testedPeer.messageReceived(message);
        lock.await(30, TimeUnit.SECONDS);
        assertTrue(expectedMessageReceived);
    }

    private boolean expectedMessageReceived;

    private MessageType expectedMessageType;

    private Message lastReceivedMessage;

    private boolean atLastOneExpected = false;

    @Subscribe
    public void handleMessageReceived(PeerMessageReceivedEvent event) {
        if (expectedMessageType != null) {
            if (!atLastOneExpected) {
                assertEquals(expectedMessageType, event.getMessage().getType());
                expectedMessageReceived = true;
            } else {
                expectedMessageReceived = event.getMessage().getType() == expectedMessageType;
            }
            lock.countDown();
        }
    }

    //@Test
    public void peerShouldRefreshPeerIdAfterHandshake() throws InterruptedException, CannotReadMessageException, THPRequestException, HashException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException {
        lock = new CountDownLatch(2);
        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.loadMetaInfo(torrentStream);
        client.refreshPeerList();

        System.out.println("Refreshing peer list");
        lock.await(30, TimeUnit.SECONDS);
        assertNotNull(peers);
        assertFalse(peers.isEmpty());

        Peer testedPeer = peers.iterator().next();

        expectedMessageReceived = false;
        expectedMessageType = MessageType.HANDSHAKE;
        lock = new CountDownLatch(2);
        testedPeer.connect();
        lock.await(30, TimeUnit.SECONDS);
        assertTrue(testedPeer.getPeerId() != null);
        System.out.println("Received peer : " + testedPeer);
    }

    //@Test
    public void peerShouldHaveConsistentPieceCount() throws InterruptedException, THPRequestException, HashException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException {
        lock = new CountDownLatch(2);
        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.loadMetaInfo(torrentStream);
        client.refreshPeerList();

        System.out.println("Refreshing peer list");
        lock.await(30, TimeUnit.SECONDS);
        assertNotNull(peers);
        assertFalse(peers.isEmpty());

        Peer testedPeer = peers.iterator().next();

        expectedMessageReceived = false;
        expectedMessageType = MessageType.HANDSHAKE;
        lock = new CountDownLatch(2);
        testedPeer.connect();
        lock.await(30, TimeUnit.SECONDS);
        assertTrue(testedPeer.getPeerId() != null);
        assertEquals(metaInfo.getInformation().getPieceCount(), testedPeer.getExpectedPieceCount());
    }

    //@Test(expected = IllegalStateException.class)
    public void peerShouldReadBitField() throws InterruptedException, THPRequestException, HashException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, CannotReadMessageException {
        lock = new CountDownLatch(2);
        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.loadMetaInfo(torrentStream);
        client.refreshPeerList();

        System.out.println("Refreshing peer list");
        lock.await(30, TimeUnit.SECONDS);
        assertNotNull(peers);
        assertFalse(peers.isEmpty());

        Peer testedPeer = peers.iterator().next();

        byte[] payload = new byte[metaInfo.getInformation().getPieceCount()];
        Arrays.fill(payload, (byte) 0xFF);
        testMessageType(testedPeer, MessageType.BITFIELD, new BitFieldMessage(payload));
        assertTrue(testedPeer.isSeeder());

        Arrays.fill(payload, (byte) 0);
        testMessageType(testedPeer, MessageType.BITFIELD, new BitFieldMessage(payload));
    }

    //@Test
    public void peerShouldReadHave() throws InterruptedException, THPRequestException, HashException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException {
        lock = new CountDownLatch(2);
        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.loadMetaInfo(torrentStream);
        client.refreshPeerList();

        System.out.println("Refreshing peer list");
        lock.await(30, TimeUnit.SECONDS);
        assertNotNull(peers);
        assertFalse(peers.isEmpty());

        Peer testedPeer = peers.iterator().next();

        testMessageType(testedPeer, MessageType.HAVE, new HaveMessage(42));
        assertFalse(testedPeer.isSeeder());
        assertTrue(testedPeer.hasPiece(42));
    }

    //@Test
    public void peerShouldDownloadBlock() throws InterruptedException, THPRequestException, HashException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, CannotReadMessageException {
        lock = new CountDownLatch(2);
        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.loadMetaInfo(torrentStream);
        client.refreshPeerList();

        System.out.println("Refreshing peer list");
        lock.await(30, TimeUnit.SECONDS);
        assertNotNull(peers);
        assertFalse(peers.isEmpty());

        expectedMessageType = MessageType.PIECE;
        expectedMessageReceived = false;
        atLastOneExpected = true;
        lock = new CountDownLatch(1);

        for (Peer testedPeer : peers) {
            testedPeer.connect();

            // We have to wait for a while until the peer has been connected to and initialized
            Thread.sleep(15000);
            // We test if the peer has some pieces
            BitSet havePieces = testedPeer.getAvailablePieces();


            if (havePieces.cardinality() > 0) {
                assertNotEquals(0, havePieces.cardinality());

                // We request the first available piece
                int pieceIndex = havePieces.nextSetBit(0);
                assertTrue(pieceIndex >= 0);

                // We need to unchoke and signal our interest to the peer first
                testedPeer.unchoke();
                testedPeer.interested();

                if (!testedPeer.isChokingUs()) {
                    // We can now request
                    testedPeer.requestPieceBlock(pieceIndex, 0);
                }
            }
        }

        // Now that the piece has been requested we need to wait for a block message
        lock.await();
        assertTrue(expectedMessageReceived);

    }

    //@Test
    public void clientShouldStart() throws InterruptedException, THPRequestException, HashException, CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException, CannotReadMessageException {
        lock = new CountDownLatch(2);
        initGuice();
        initEventBus();
        initTorrentStream();
        initClient();

        client.configure(
                ClientSettings.settingsBuilder()
                        .basePath(Paths.get(this.getClass().getResource("").getPath()))
                        .metainfoSource(torrentStream)
        );

        client.start();

        while(true) {
            Thread.sleep(200);
        }
    }



}
