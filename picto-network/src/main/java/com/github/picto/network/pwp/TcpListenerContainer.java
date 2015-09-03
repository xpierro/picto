package com.github.picto.network.pwp;

import java.util.ArrayList;
import java.util.List;

/**
 * Container creating TCP listeners and providing relay interfaces to them.
 * Created by Pierre on 02/09/15.
 */
public abstract class TcpListenerContainer {

    private final List<TcpListener> listeners;

    public TcpListenerContainer() {
        listeners = new ArrayList<>();
    }

    public TcpListener createNewListener(int port) throws InterruptedException {
        TcpListener tcpListener = new TcpListener(port) {
            @Override
            protected void emitWire(final PeerWire peerWire) {
                emitWire(peerWire);
            }
        };
        listeners.add(tcpListener);
        return tcpListener;
    }

    public abstract void emitWire(final PeerWire peerWire);
}
