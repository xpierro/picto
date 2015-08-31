package com.github.picto.php.model;

import java.net.InetAddress;

/**
 * Represents a peer on the network.
 * Created by Pierre on 31/08/15.
 */
public class Peer {
    private InetAddress host;
    private int port;

    private byte[] peerId;

    public Peer() {

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
}
