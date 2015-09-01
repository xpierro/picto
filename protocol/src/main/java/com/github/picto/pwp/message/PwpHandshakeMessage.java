package com.github.picto.pwp.message;

import com.github.picto.util.ByteArrayUtils;

import java.io.UnsupportedEncodingException;

/**
 * Required first message sent to a peer.
 * Created by Pierre on 01/09/15.
 */
public class PwpHandshakeMessage {
    private static final int INFO_HASH_LENGTH = 20;
    private static final int PEER_ID_LENGTH = 20;
    private static final int RESERVED_LENGTH = 8;

    private static enum Protocol {
        BITORRENT_1("BitTorrent protocol");

        private final String pstr;

        private Protocol(final String pstr) {
            this.pstr = pstr;
        }

        public byte[] getPstrBytes() {
            try {
                return pstr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("UTF must be supported by the JVM");
            }
        }
    }

    private byte pstrLength;

    private byte[] pstr;

    private byte[] reserved;

    private byte[] infoHash;

    private byte[] peerId;

    public PwpHandshakeMessage() {
        pstr = Protocol.BITORRENT_1.getPstrBytes();
        pstrLength = (byte) pstr.length;

        reserved = new byte[RESERVED_LENGTH];
        ByteArrayUtils.fillWithZeroes(reserved);

        infoHash = new byte[INFO_HASH_LENGTH];
        peerId = new byte[PEER_ID_LENGTH];
    }
}
