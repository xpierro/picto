package com.github.picto.protocol.thp.model.peerid;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Collection of static, known peer ids, to mimic other clients.
 * Created by Pierre on 29/08/15.
 */
public enum StaticPeerId implements PeerId {
    //TODO: extract version number somehow
    //TODO: build full list of known clients
    //TODO: find enum by client name/version
    DEFAULT("-PI0001-", "Picto", "1");

    /**
     * The maximum size of the peer id array
     */
    private static final int MAX_LENGTH = 20;

    private final Charset DEFAULT_PEER_ID_CHARSET = StandardCharsets.UTF_8;

    private byte[] peerId;
    private String clientName;
    private String clientVersion;

    private StaticPeerId(final String peerId, final String clientName, final String clientVersion) {
        if (peerId.length() > MAX_LENGTH) {
            throw new IllegalStateException(String.format("Cannot build a peer id with more than %d characters", MAX_LENGTH));
        }

        if (peerId.length() < MAX_LENGTH) {
            this.peerId = Arrays.copyOf(peerId.getBytes(DEFAULT_PEER_ID_CHARSET), MAX_LENGTH);

            Random random = new Random(new Date().getTime());
            for (int i = peerId.length(); i < 20; i++) {
                this.peerId[i] = (byte) random.nextInt();
            }
        } else {
            this.peerId = peerId.getBytes(DEFAULT_PEER_ID_CHARSET);
        }
        this.clientName = clientName;
        this.clientVersion = clientVersion;
    }

    @Override
    public byte[] getPeerIdBytes() {
        return peerId;
    }
}
