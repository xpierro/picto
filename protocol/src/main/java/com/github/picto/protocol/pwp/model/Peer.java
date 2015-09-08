package com.github.picto.protocol.pwp.model;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Represents a peer on the network.
 * Created by Pierre on 31/08/15.
 */
public class Peer {
    private InetAddress host;
    private int port;

    private byte[] peerId;

    private boolean chokedByUs;
    private boolean interestingForUs;

    private boolean chokingUs;
    private boolean interestedInUs;

    public Peer() {
        chokedByUs = true;
        interestingForUs = false;
        chokingUs = true;
        interestedInUs = false;
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
