package com.github.picto.thp.model.peerid;

/**
 * Intermediate class validating peer id size.
 * Created by Pierre on 29/08/15.
 */
public abstract class AbstractPeerId implements PeerId {

    @Override
    public final byte[] getPeerIdBytes() {
        byte[] peerId = getPeerId();
        if (peerId.length != 20) {
            throw new IllegalStateException("Impossible to have a peer id shorter or longer than 20 bytes");
        }
        return peerId;
    }

    protected abstract byte[] getPeerId();
}
