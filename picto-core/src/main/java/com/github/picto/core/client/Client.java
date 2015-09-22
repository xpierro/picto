package com.github.picto.core.client;

import com.github.picto.bencode.BEncodeReader;
import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.exception.CannotUnserializeException;
import com.github.picto.bencode.serialization.BEncodeUnserializer;
import com.github.picto.bencode.type.BEncodeableDictionary;
import com.github.picto.bencode.type.BEncodeableType;
import com.github.picto.filesystem.FilesystemService;
import com.github.picto.filesystem.IFilesystemMetainfo;
import com.github.picto.filesystem.event.FilesystemReadyEvent;
import com.github.picto.network.http.GetExecutor;
import com.github.picto.network.http.event.HttpResponseReceivedEvent;
import com.github.picto.network.pwp.PeerWire;
import com.github.picto.network.pwp.TcpConnecter;
import com.github.picto.network.pwp.event.NewPeerWireEvent;
import com.github.picto.network.pwp.message.PieceMessage;
import com.github.picto.network.pwp.message.PwpHandshakeMessage;
import com.github.picto.protocol.event.*;
import com.github.picto.protocol.metainfo.model.IMetaInfoFileDescription;
import com.github.picto.protocol.metainfo.model.MetaInfo;
import com.github.picto.protocol.pwp.exception.BlockAlreadyDownloadedException;
import com.github.picto.protocol.pwp.exception.InvalidBlockSizeException;
import com.github.picto.protocol.pwp.model.Peer;
import com.github.picto.protocol.pwp.model.Piece;
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
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The client downloading a file described by a meta-info.
 *
 * Created by Pierre on 08/09/15.
 */
public class Client {

    public static final int DEFAULT_MAX_CONNECTION = 50;

    private EventBus eventBus;

    @Inject
    private GetExecutor getExecutor;

    @Inject
    private TcpConnecter tcpConnecter;

    @Inject
    private Provider<Peer> peerProvider;

    @Inject
    private FilesystemService filesystemService;

    private String fileName;

    public static enum PieceStatus {
        // The piece isnt present and hasn't been downloaded at all.
        DO_NOT_HAVE,
        // We have the piece
        HAVE,
        // The piece has started being requested but no download have been launched
        REQUESTED,
        // The hash value of the piece is wrong. This piece should be re-downloaded.
        CORRUPTED,
        // The piece is partially downloaded.
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
     * TODO: should we use a bitset per status to do binary operation easily ? Eg: how do we know pieces clients have that we don't ?
     */
    private PieceStatus[] piecesStatus;

    /**
     * Pieces that have been loaded in memory either for download or upload.
     * Downloading pieces should be flushed to file system as soon as downloaded, while transferring pieces should
     * be pruned when a certain delay has passed since they've been requested last.
     */
    private Map<Integer, Piece> inMemoryPieces;

    private final Map<InetAddress, Peer> peers;

    private int announceInterval = 0;
    private ThpAnnounceEvent currentAnnounceEvent = ThpAnnounceEvent.STARTED;
    private int seedersNb = 0;
    private int leechersNb = 0;

    /**
     * The number of currently open connections.
     */
    private int openConnections;

    //TODO: inject a DateUtil.currentDate for testability
    private Date lastAnnounce = new Date();

    private ClientSettings clientSettings;

    @Inject
    public Client(EventBus eventBus) {
        this.peers = new HashMap<>();
        listenPort = Optional.empty();

        openConnections = 0;

        peerId = StaticPeerId.DEFAULT;

        inMemoryPieces = new HashMap<>();

        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public void configure(ClientSettings clientSettings) {
        this.clientSettings = clientSettings;
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    // TODO: verify that the set is indeed allowing us to maintain existing peers.
    public Collection<Peer> getPeers() {
        return peers.values();
    }

    // Client lifecycle

    public void start() throws CannotUnserializeException, CannotReadTokenException, CannotReadBencodedException {
        loadMetaInfo(clientSettings.getMetainfoSource());
    }

    public void setBasePath(Path basePath) {
        clientSettings.basePath(basePath);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

        piecesStatus = new PieceStatus[metaInfo.getInformation().getPieceCount()];
        Arrays.fill(piecesStatus, PieceStatus.DO_NOT_HAVE);

        fireMetaInfoLoaded();
    }

    /**
     * Initialize file system management, create folder structure etc.
     */
    public void initFilesystem() {
        // We first initialize the meta info.
        IFilesystemMetainfo filesystemMetainfo = filesystemService.getFilesystemMetainfo();
        // TODO: handle base path change
        filesystemMetainfo.setBasePath(clientSettings.getBasePath());
        filesystemMetainfo.setPieceCount(metaInfo.getInformation().getPieceCount());
        filesystemMetainfo.setPieceLength(metaInfo.getInformation().getPieceLength());

        if (metaInfo.getInformation().isMultifiles()) {
            for (IMetaInfoFileDescription metaInfoFileDescription : metaInfo.getInformation().getFiles()) {
                //TODO: file names
                filesystemMetainfo.addFileInformation(metaInfoFileDescription.getPath(), metaInfoFileDescription.getPath(), metaInfoFileDescription.getLength());
            }
        } else {
            if (fileName == null) {
                fileName = metaInfo.getInformation().getName();
            }
            filesystemMetainfo.addFileInformation(fileName, fileName, metaInfo.getInformation().getLength());
        }

        filesystemService.initializeFilesystem();
    }

    @Subscribe
    public void handleFilesystemLoaded(FilesystemReadyEvent event) throws THPRequestException, HashException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "The file system has been created, torrent is ready to download.");
        refreshPeerList();
    }

    private void fireMetaInfoLoaded() {
        eventBus.post(new MetaInfoLoadedEvent());
    }

    @Subscribe
    public void handleMetaInfoLoaded(MetaInfoLoadedEvent event) throws THPRequestException, HashException {
        initFilesystem();
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
                // TODO: This initialization is awkward, make it better. Injecting the meta info in the client scope could be an idea.
                peer.setExpectedPieceCount(metaInfo.getInformation().getPieceCount());
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

    @Subscribe
    public void handlePeerListChanged(PeerListChangedEvent event) throws HashException {
        while (openConnections < clientSettings.getMaxConnections()) {
            connectNextPeer();
        }
    }

    /**
     * Connects to the next available peer.
     */
    public void connectNextPeer() throws HashException {
        for (Peer peer : getPeers()) {
            if (!peer.isConnected() && !peer.isConnecting()) {
                connectToPeer(peer);
            }
        }
    }

    /**
     * Change the number of authorized connections for this client.
     * Default is 50.
     * @param maxConnections The new limit of connections that can be opened by this client. Has to be superior to 0.
     */
    public void setMaxConnections(final int maxConnections) {
        if (maxConnections == 0) {
            throw new IllegalStateException("Cannot connect to 0 peers");
        }
        this.clientSettings.maxConnections(maxConnections);
        fireMaxConnectionsChanged();
    }

    private void fireMaxConnectionsChanged() {
        eventBus.post(new MaxConnectionsChangedEvent());
    }

    public void connectToPeer(final Peer peer) throws HashException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Attempting to connect to " + peer);
        openConnections += 1;
        peer.connect();
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

    @Subscribe
    public void handleMessage(PeerMessageReceivedEvent event) throws BlockAlreadyDownloadedException, InvalidBlockSizeException {
        switch (event.getMessage().getType()) {
            case PIECE:
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "A piece has been received, saving to memory.");
                PieceMessage pieceMessage = (PieceMessage) event.getMessage();

                if (!inMemoryPieces.containsKey(pieceMessage.getPieceIndex())) {
                    inMemoryPieces.put(pieceMessage.getPieceIndex(), new Piece(pieceMessage.getPieceIndex(), metaInfo.getInformation().getPieceLength()));
                    piecesStatus[pieceMessage.getPieceIndex()] = PieceStatus.DOWNLOADING;
                }
                Piece piece = inMemoryPieces.get(pieceMessage.getPieceIndex());
                piece.insertBlock(pieceMessage.getByteOffset(), pieceMessage.getBlock());
                if (piece.isPieceComplete()) {
                    piecesStatus[pieceMessage.getPieceIndex()] = PieceStatus.HAVE;
                    filesystemService.savePiece(pieceMessage.getPieceIndex(), piece.getPieceContent());
                }
                break;
            case HANDSHAKE:
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "A peer has connected and is ready for messages.");
                event.getPeer().setConnected();
                break;
        }
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
