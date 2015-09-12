package com.github.picto.protocol.client;

import com.github.picto.bencode.BEncodeReader;
import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.exception.CannotUnserializeException;
import com.github.picto.bencode.serialization.BEncodeUnserializer;
import com.github.picto.bencode.type.BEncodeableDictionary;
import com.github.picto.bencode.type.BEncodeableType;
import com.github.picto.network.http.GetExecutor;
import com.github.picto.network.http.event.HttpResponseReceivedEvent;
import com.github.picto.network.pwp.PeerWire;
import com.github.picto.network.pwp.TcpConnecter;
import com.github.picto.network.pwp.event.NewPeerWireEvent;
import com.github.picto.network.pwp.message.PwpHandshakeMessage;
import com.github.picto.protocol.event.MaxConnectionsChangedEvent;
import com.github.picto.protocol.event.MetaInfoLoadedEvent;
import com.github.picto.protocol.event.NewConnectedPeerEvent;
import com.github.picto.protocol.event.PeerListChangedEvent;
import com.github.picto.protocol.metainfo.model.MetaInfo;
import com.github.picto.protocol.pwp.model.Peer;
import com.github.picto.protocol.thp.exception.THPRequestException;
import com.github.picto.protocol.thp.model.ThpAnnounceEvent;
import com.github.picto.protocol.thp.model.TrackerAnnounceResponseModel;
import com.github.picto.protocol.thp.model.peerid.PeerId;
import com.github.picto.protocol.thp.model.peerid.StaticPeerId;
import com.github.picto.protocol.thp.request.AnnounceGet;
import com.github.picto.util.Hasher;
import com.github.picto.util.exception.HashException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The client downloading a file described by a meta-info.
 *
 * Created by Pierre on 08/09/15.
 */
public class Client {

    private static final int DEFAULT_MAX_CONNECTION = 50;

    private EventBus eventBus;

    @Inject
    private GetExecutor getExecutor;

    @Inject
    private TcpConnecter tcpConnecter;

    @Inject
    private Provider<Peer> peerProvider;

    public static enum PieceStatus {
        DO_NOT_HAVE,
        HAVE,
        REQUESTED,
        CORRUPTED,
        DOWNLOADING
    }

    /**
     * The metaInfo that client will download.
     */
    private MetaInfo metaInfo;

    /**
     * The port the client listens on for new peer connections.
     */
    private Optional<Integer> listenPort;

    /**
     * The client peerId
     */
    private PeerId peerId;

    /**
     * The status for each piece.
     */
    private PieceStatus[] piecesStatus;

    private final Map<InetAddress, Peer> peers;

    private int announceInterval = 0;
    private ThpAnnounceEvent currentAnnounceEvent = ThpAnnounceEvent.STARTED;
    private int seedersNb = 0;
    private int leechersNb = 0;

    /**
     * The maximum number of allowed connections. 50 by default.
     */
    private int maxConnections;

    //TODO: inject a DateUtil.currentDate for testability
    private Date lastAnnounce = new Date();

    @Inject
    public Client(EventBus eventBus) {
        this.peers = new HashMap<>();
        listenPort = Optional.empty();

        maxConnections = DEFAULT_MAX_CONNECTION;

        peerId = StaticPeerId.DEFAULT;

        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    // TODO: verify that the set is indeed allowing us to maintain existing peers.
    public Collection<Peer> getPeers() {
        return peers.values();
    }

    // Client lifecycle

    public void start() {

    }

    /**
     * Loads the meta-info file
     * @param metaInfoSource The inputstream representing an underlying meta-info file.
     * @throws CannotReadTokenException If a token is missing in the bencoded stream.
     * @throws CannotReadBencodedException If the metainfo isn't correctly bencoded.
     * @throws CannotUnserializeException If the meta-info file is malformed.
     */
    //TODO: makes this asynchronous ?
    public void loadMetaInfo(final InputStream metaInfoSource) throws CannotReadTokenException, CannotReadBencodedException, CannotUnserializeException {
        BEncodeableDictionary dictionary = (BEncodeableDictionary) new BEncodeReader(metaInfoSource).readBencodable();

        BEncodeUnserializer<MetaInfo> unserializer = new BEncodeUnserializer<>(dictionary, MetaInfo.class);
        metaInfo = unserializer.unserialize();

        fireMetaInfoLoaded();
    }

    private void fireMetaInfoLoaded() {
        eventBus.post(new MetaInfoLoadedEvent());
    }

    public void setupPeerListener() {

    }

    public void setListenPort(int listenPort) {
        this.listenPort = Optional.of(listenPort);
    }

    /**
     * Refresh the peer list from the meta-info file.
     * @throws HashException
     * @throws THPRequestException
     */
    public void refreshPeerList() throws HashException, THPRequestException {
        if (!listenPort.isPresent()) {
            throw new IllegalStateException("Impossible to setup a client without a port set up.");
        }

        URI uri = new AnnounceGet()
                .metaInfo(metaInfo)
                .announceURI(metaInfo.getAnnounce())
                .port(listenPort.get())
                .compact(true) //TODO: make it parametrizable
                .peerId(StaticPeerId.DEFAULT) // TODO: make it parametrizable
                .event(currentAnnounceEvent) // TODO: make it more dynamic (handle all status)
                .build()
                .getUri();

        getExecutor.execute(uri);
    }

    @Subscribe
    public void peerListRefreshed(HttpResponseReceivedEvent event) throws CannotReadTokenException, CannotReadBencodedException, CannotUnserializeException {
        BEncodeReader bEncodeReader = new BEncodeReader(new ByteArrayInputStream(event.getBytes()));
        BEncodeableType type = bEncodeReader.readBencodable();
        TrackerAnnounceResponseModel response = new BEncodeUnserializer<>((BEncodeableDictionary) type, TrackerAnnounceResponseModel.class).unserialize();

        if (response.getFailureReason() == null) {
            announceInterval = response.getInterval();
            currentAnnounceEvent = ThpAnnounceEvent.REGULAR;
            seedersNb = response.getComplete();
            leechersNb = response.getIncomplete();

            //TODO: testability
            lastAnnounce = new Date();

            // Some peers might have already been stored, we don't want to erase them as they have wires opened
            response.getPeers(peerProvider).stream().filter(peer -> !peers.containsKey(peer.getHost())).forEach(peer -> {
                peers.put(peer.getHost(), peer);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "A new peer information has been received from the tracker: " + peer);
            });

            //TODO: make a better message
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "The peer list has been refreshed.");
            firePeerListChanged();
        }
    }

    private void firePeerListChanged() {
        eventBus.post(new PeerListChangedEvent());
    }

    public void setMaxConnections(final int maxConnections) {
        if (maxConnections == 0) {
            throw new IllegalStateException("Cannot connect to 0 peers");
        }
        this.maxConnections = maxConnections;
        fireMaxConnectionsChanged();
    }

    private void fireMaxConnectionsChanged() {
        eventBus.post(new MaxConnectionsChangedEvent());
    }

    public void connectToPeer(final Peer peer) throws HashException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Attempting to connect to " + peer);
        tcpConnecter.connect(
                peer.getHost(),
                peer.getPort()
        );
    }

    @Subscribe
    public void handleNewPeerWire(final NewPeerWireEvent peerWireEvent) throws HashException {
        final PeerWire peerWire = peerWireEvent.getPeerWire();

        // We erase any old peer information from that host.
        InetAddress peerAddress = peerWire.getHost();
        final Peer peer;
        if (peers.containsKey(peerAddress)) {
            peer = peers.get(peerAddress);
            peer.setPeerWire(peerWire);
        } else {
            peer = peerProvider.get();
            peer.setPeerWire(peerWire);
            peers.put(peerAddress, peer);
        }
        fireNewConnectedPeer(peer);
    }

    private void fireNewConnectedPeer(final Peer peer) throws HashException {
        // We send our handshake
        peer.sendMessage(new PwpHandshakeMessage()
                .infoHash(Hasher.sha1(metaInfo.getInformation().getBEncodeableDictionary().getBEncodedBytes()))
                .peerId(peerId.getPeerIdBytes()));
        eventBus.post(new NewConnectedPeerEvent());
    }

    private void prioritizePieces() {

    }

    private void requestNextPiece() {

    }

    private void persistPiece() {

    }

    private void refreshPiecesStatus() {

    }

    // Client request
    private void requestPiece(int pieceIndex) {

    }

    private void sendPiece(int pieceIndex, Peer peer) {

    }

    // Informations
    private double getFilePercentage() {
        return 0.;
    }
}
