package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.annotation.PwpMessageFragment;
import com.github.picto.util.ByteArrayUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Required first message sent to a peer.
 * Created by Pierre on 01/09/15.
 */
public class PwpHandshakeMessage implements Message {
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

    public PwpHandshakeMessage(final byte[] payload) {
        readPayload(payload);
    }

    public PwpHandshakeMessage peerId(byte[] peerId) {
        this.peerId = Arrays.copyOf(peerId, peerId.length);
        return this;
    }

    public PwpHandshakeMessage infoHash(byte[] infoHash) {
        this.infoHash = Arrays.copyOf(infoHash, infoHash.length);
        return this;
    }
    
    // TODO: make a generic version
    @Override
    public byte[] getRawBytes() {
        int length = 68;
        byte[] bytes = new byte[length];

        bytes[0] = getPstrLength();

        int position = 1;
        System.arraycopy(getPstr(), 0, bytes, position, getPstr().length);
        position += getPstr().length;
        System.arraycopy(getReserved(), 0, bytes, position, 8);
        position += 8;
        System.arraycopy(getInfoHash(), 0, bytes, position, INFO_HASH_LENGTH);
        position += INFO_HASH_LENGTH;
        System.arraycopy(getPeerId(), 0, bytes, position, PEER_ID_LENGTH);

        return bytes;
    }

    @Override
    public MessageType getType() {
        return MessageType.HANDSHAKE;
    }

    @Override
    public int getMessageId() {
        return -1;
    }

    @Override
    public int getMessageLength() {
        return 68;
    }

    // TODO: make a generic version
    private void readPayload(byte[] payload) {

        pstrLength = payload[0];
        int position = 1;
        pstr = new byte[pstrLength];
        System.arraycopy(payload, position, pstr, 0, pstrLength);
        position += getPstr().length;

        reserved = new byte[8];
        System.arraycopy(payload, position, reserved, 0, 8);
        position += 8;

        infoHash = new byte[INFO_HASH_LENGTH];
        System.arraycopy(payload, position, infoHash, 0, INFO_HASH_LENGTH);
        position += INFO_HASH_LENGTH;

        peerId = new byte[PEER_ID_LENGTH];
        System.arraycopy(payload, position, peerId, 0, PEER_ID_LENGTH);

    }

    @PwpMessageFragment(length = 1, order = 1)
    public byte getPstrLength() {
        return pstrLength;
    }
    
    @PwpMessageFragment(order = 2)
    public byte[] getPstr() {
        return pstr;
    }

    @PwpMessageFragment(length = 8, order = 3)
    public byte[] getReserved() {
        return reserved;
    }

    @PwpMessageFragment(length = INFO_HASH_LENGTH, order = 4)
    public byte[] getInfoHash() {
        return infoHash;
    }

    @PwpMessageFragment(length = PEER_ID_LENGTH, order = 5)
    public byte[] getPeerId() {
        return peerId;
    }
}
