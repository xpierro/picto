package com.github.picto.protocol.pwp.model;

import com.github.picto.network.pwp.PeerWire;
import com.github.picto.network.pwp.TcpConnecter;
import com.github.picto.network.pwp.message.BitFieldMessage;
import com.github.picto.network.pwp.message.HaveMessage;
import com.github.picto.network.pwp.message.Message;
import com.github.picto.network.pwp.message.PwpHandshakeMessage;
import com.github.picto.protocol.event.PeerMessageReceivedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Represents a peer on the network.
 * Created by Pierre on 31/08/15.
 */
public class Peer {

    private static final String UNKNOWN_PEER_ID = "UNKNOWN";

    @Inject
    private EventBus eventBus;

    @Inject
    private TcpConnecter tcpConnecter;

    private InetAddress host;
    private int port;

    private byte[] peerId;

    private boolean chokedByUs;
    private boolean interestingForUs;

    private boolean chokingUs;
    private boolean interestedInUs;

    private PeerWire peerWire;

    /**
     * Array of boolean representing pieces the peer has or not to share. If all pieces are possessed, the peer is a
     * seeder.
     */
    private BitSet havePieces;

    /**
     * Expected number of pieces. A peer cannot ask or provide pieces above that maximum number.
     */
    private int expectedPieceCount;

    public Peer() {
        chokedByUs = true;
        interestingForUs = false;
        chokingUs = true;
        interestedInUs = false;
        expectedPieceCount = -1;
    }

    /**
     * Subscribe to the internal event bus of the underlying peer wire.
     */
    private void listenToWire() {
        if (peerWire == null) {
            throw new IllegalStateException("Cannot register on an unexisting peer wire.");
        }
        peerWire.listenToWire(this);
    }

    public void setPeerWire(PeerWire peerWire) {
        this.peerWire = peerWire;
        this.host = peerWire.getHost();
        listenToWire();
    }

    public void setExpectedPieceCount(final int pieceCount) {
        havePieces = new BitSet(pieceCount);
        expectedPieceCount = pieceCount;
    }

    public boolean havePiece(final int pieceIndex) {
        if (pieceIndex >= expectedPieceCount) {
            throw new IllegalStateException("The requested piece index is invalid : " + pieceIndex + " (out of " + expectedPieceCount);
        }
        return havePieces.get(pieceIndex);
    }

    public boolean isSeeder() {
        return expectedPieceCount > 0 && havePieces.cardinality() == expectedPieceCount;
    }

    public int getExpectedPieceCount() {
        return expectedPieceCount;
    }

    public PeerWire getPeerWire() {
        return peerWire;
    }

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getPeerId() {
        return peerId;
    }

    public String getPeerIdString(final Charset charset) {
        if (peerId == null) {
            return UNKNOWN_PEER_ID;
        }
        return new String(peerId, charset);
    }

    public void setPeerId(byte[] peerId) {
        this.peerId = peerId;
    }

    public boolean isChokedByUs() {
        return chokedByUs;
    }

    public void setChokedByUs(boolean chokedByUs) {
        this.chokedByUs = chokedByUs;
    }

    public boolean isInterestingForUs() {
        return interestingForUs;
    }

    public void setInterestingForUs(boolean interestingForUs) {
        this.interestingForUs = interestingForUs;
    }

    public boolean isChokingUs() {
        return chokingUs;
    }

    public void setChokingUs(boolean chokingUs) {
        this.chokingUs = chokingUs;
    }

    public boolean isInterestedInUs() {
        return interestedInUs;
    }

    public void setInterestedInUs(boolean interestedInUs) {
        this.interestedInUs = interestedInUs;
    }

    public void sendMessage(final Message message) {
        peerWire.sendMessage(message);
    }

    @Subscribe
    public void messageReceived(final Message message) {
        switch (message.getType()) {
            case CHOKE:
                setChokingUs(true);
                break;
            case UNCHOKE:
                setChokingUs(false);
                break;
            case INTERESTED:
                setInterestedInUs(true);
                break;
            case NOT_INTERESTED:
                setInterestedInUs(false);
                break;
            case HANDSHAKE:
                this.peerId = ((PwpHandshakeMessage) message).getPeerId();
                break;
            case BITFIELD:
                BitFieldMessage bitFieldMessage = (BitFieldMessage) message;
                if (havePieces != null) {
                    throw new IllegalStateException("A bitfield message has been received after the piece status has been set.");
                }
                havePieces = bitFieldMessage.getBitSet();
                break;
            case HAVE:
                HaveMessage haveMessage = (HaveMessage) message;
                havePieces.set(haveMessage.getPieceIndex());
                break;

        }

        eventBus.post(new PeerMessageReceivedEvent(this, message));
    }

    public void connect() {
        tcpConnecter.connect(
                getHost(),
                getPort()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        if (port != peer.port) return false;
        if (!host.equals(peer.host)) return false;
        if (!Arrays.equals(peerId, peer.peerId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + Arrays.hashCode(peerId);
        return result;
    }

    @Override
    public String toString() {
        return String.format("{Peer ip: %s, port: %s, id: %s}", host, port, getPeerIdString(Charset.forName("UTF-8")));
    }
}
