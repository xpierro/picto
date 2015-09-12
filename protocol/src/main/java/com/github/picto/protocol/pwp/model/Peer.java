package com.github.picto.protocol.pwp.model;

import com.github.picto.network.pwp.PeerWire;
import com.github.picto.network.pwp.message.Message;
import com.github.picto.protocol.event.PeerMessageReceivedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Represents a peer on the network.
 * Created by Pierre on 31/08/15.
 */
public class Peer {

    @Inject
    private EventBus eventBus;

    private InetAddress host;
    private int port;

    private byte[] peerId;

    private boolean chokedByUs;
    private boolean interestingForUs;

    private boolean chokingUs;
    private boolean interestedInUs;

    private PeerWire peerWire;

    public Peer() {
        chokedByUs = true;
        interestingForUs = false;
        chokingUs = true;
        interestedInUs = false;
    }

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
        }

        eventBus.post(new PeerMessageReceivedEvent(this, message));
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
        return String.format("{Peer ip: %s, port: %s, id: %s}", host, port, peerId == null ? "UNIDENTIFIED" : new String(peerId, Charset.forName("UTF-8")));
    }
}
