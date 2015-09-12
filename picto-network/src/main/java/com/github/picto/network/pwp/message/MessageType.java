package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all the possible Bitorrent message types.
 *
 * Created by Pierre on 06/09/15.
 */
public enum MessageType {
    HANDSHAKE(19),
    CHOKE(0),
    UNCHOKE(1),
    INTERESTED(2),
    NOT_INTERESTED(3),
    HAVE(4),
    BITFIELD(5),
    REQUEST(6),
    PIECE(7),
    CANCEL(8),
    PORT(9),
    KEEPALIVE(-1);

    private static final Map<Byte, MessageType> lookupMap;

    static {
        lookupMap = new HashMap<>();
        for (MessageType messageType : values()) {
            lookupMap.put(messageType.getId(), messageType);
        }
    }

    private byte id;

    private MessageType(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }

    public static MessageType findById(byte id) throws CannotReadMessageException {
        if (lookupMap.containsKey(id)) {
            return lookupMap.get(id);
        }
        throw new CannotReadMessageException("Impossible to read message of id : " + id);
    }
}
