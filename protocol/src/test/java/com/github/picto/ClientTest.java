package com.github.picto;

import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.exception.CannotUnserializeException;
import com.github.picto.module.ProtocolModule;
import com.github.picto.protocol.client.Client;
import com.github.picto.protocol.event.MetaInfoLoadedEvent;
import com.github.picto.protocol.event.NewConnectedPeerEvent;
import com.github.picto.protocol.event.PeerListChangedEvent;
import com.github.picto.protocol.metainfo.model.MetaInfo;
import com.github.picto.protocol.pwp.model.Peer;
import com.github.picto.protocol.thp.exception.THPRequestException;
import com.github.picto.util.exception.HashException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import java.io.InputStream;
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
        Injector injector = Guice.createInjector(new ProtocolModule());
        client = injector.getInstance(Client.class);
        eventBus = injector.getInstance(EventBus.class);
    }

    private void initEventBus() {
        eventBus.register(this);
    }

    private void initTorrentStream() {
        torrentStream = this.getClass().getClassLoader().getResourceAsStream("com/github/picto/protocol/metainfo/ubuntu.torrent");
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

    @Test
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

    @Test
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

    @Test
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

}
