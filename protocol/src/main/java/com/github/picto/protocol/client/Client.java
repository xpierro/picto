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
import com.github.picto.protocol.event.MetaInfoLoadedEvent;
import com.github.picto.protocol.event.PeerListChangedEvent;
import com.github.picto.protocol.metainfo.model.MetaInfo;
import com.github.picto.protocol.pwp.model.Peer;
import com.github.picto.protocol.thp.exception.THPRequestException;
import com.github.picto.protocol.thp.model.ThpAnnounceEvent;
import com.github.picto.protocol.thp.model.TrackerAnnounceResponseModel;
import com.github.picto.protocol.thp.model.peerid.StaticPeerId;
import com.github.picto.protocol.thp.request.AnnounceGet;
import com.github.picto.util.exception.HashException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

    private EventBus eventBus;

    @Inject
    private GetExecutor getExecutor;

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
     * The status for each piece.
     */
    private PieceStatus[] piecesStatus;

    private final Set<Peer> peers;

    private int announceInterval = 0;
    private ThpAnnounceEvent currentAnnounceEvent = ThpAnnounceEvent.STARTED;
    private int seedersNb = 0;
    private int leechersNb = 0;

    //TODO: inject a DateUtil.currentDate for testability
    private Date lastAnnounce = new Date();

    @Inject
    public Client(EventBus eventBus) {
        this.peers = new HashSet<>();
        listenPort = Optional.empty();

        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    // TODO: verify that the set is indeed allowing us to maintain existing peers.
    public Set<Peer> getPeers() {
        return peers;
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

            peers.addAll(response.getPeers());

            //TODO: make a better message
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "The peer list has been refreshed for the client");
            firePeerListChanged();
        }


    }

    private void firePeerListChanged() {
        eventBus.post(new PeerListChangedEvent());
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
