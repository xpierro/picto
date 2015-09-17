package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory creating message from byte arrays.
 *
 * Created by Pierre on 06/09/15.
 */
public class MessageFactory {

    /**
     * Returns a concrete interpretable message
     * @param bytes
     * @return
     */
    public static Message getMessage(byte[] bytes) throws CannotReadMessageException {
        Message message;


        MessageType messageType;

        if (bytes.length == 0) {
            messageType = MessageType.KEEPALIVE;
        } else {
            byte typeId = bytes[0];
            messageType = MessageType.findById(typeId);
        }

        //TODO: is this efficient ?
        byte[] payload = Arrays.copyOfRange(bytes, 1, bytes.length);

        switch (messageType) {
            case HANDSHAKE:
                message = new PwpHandshakeMessage(bytes);
                break;
            case CHOKE:
                message = new ChokeMessage(payload);
                break;
            case UNCHOKE:
                message = new UnChokeMessage(payload);
                break;
            case INTERESTED:
                message = new InterestedMessage(payload);
                break;
            case NOT_INTERESTED:
                message = new NotInterestedMessage(payload);
                break;
            case HAVE:
                message = new HaveMessage(payload);
                break;
            case BITFIELD:
                message = new BitFieldMessage(payload);
                break;
            case REQUEST:
                message = new RequestMessage(payload);
                break;
            case PIECE:
                message = new PieceMessage(payload);
                break;
            case CANCEL:
                message = new CancelMessage(payload);
                break;
            case PORT:
                message = new PortMessage(payload);
                break;
            case KEEPALIVE:
                message = new KeepAliveMessage(payload);
                break;
            default:
                throw new CannotReadMessageException("Impossible to decode the message type " + messageType);
        }

        Logger.getLogger(MessageFactory.class.getName()).log(Level.INFO, "Received a message of type " + messageType + " (" + message + ")");

        return message;
    }

}
