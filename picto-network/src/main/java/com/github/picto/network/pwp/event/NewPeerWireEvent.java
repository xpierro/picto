package com.github.picto.network.pwp.event;

import com.github.picto.network.pwp.PeerWire;

/**
 * Signals that a new peer wire has been created after a successful handshake.
 * Created by Pierre on 12/09/15.
 */
public class NewPeerWireEvent {

    private final PeerWire peerWire;

    public NewPeerWireEvent(final PeerWire peerWire) {
        this.peerWire = peerWire;
    }

    public PeerWire getPeerWire() {
        return peerWire;
    }
}
