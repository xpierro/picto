package com.github.picto.thp.model.peerid;

/**
 * Interface providing a Peer ID modelization.
 * Created by Pierre on 29/08/15.
 */
public interface PeerId {
    /**
     * Returns a 20 bytes char array representing the peer id.
     * @return 20-bytes, random or not, client identifier for a tracker. Must be the same for all request for a particular
     * torrent.
     */
    byte[] getPeerIdBytes();
}
