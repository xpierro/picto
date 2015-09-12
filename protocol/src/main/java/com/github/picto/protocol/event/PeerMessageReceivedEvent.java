package com.github.picto.protocol.event;

import com.github.picto.network.pwp.message.Message;
import com.github.picto.protocol.pwp.model.Peer;

/**
 * Created by Pierre on 12/09/15.
 */
public class PeerMessageReceivedEvent {

    private final Peer peer;
    private final Message message;

    public PeerMessageReceivedEvent(final Peer peer, final Message message) {
        this.peer = peer;
        this.message = message;
    }

    public Peer getPeer() {
        return peer;
    }

    public Message getMessage() {
        return message;
    }
}
