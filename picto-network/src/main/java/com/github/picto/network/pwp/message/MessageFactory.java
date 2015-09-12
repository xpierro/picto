package com.github.picto.network.pwp.message;

import com.github.picto.network.pwp.exception.CannotReadMessageException;

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

        switch (messageType) {
            case HANDSHAKE:
                message = new PwpHandshakeMessage(bytes);
                break;
            case CHOKE:
                message = new ChokeMessage(bytes);
                break;
            case UNCHOKE:
                message = new UnChokeMessage(bytes);
                break;
            case INTERESTED:
                message = new InterestedMessage(bytes);
                break;
            case NOT_INTERESTED:
                message = new NotInterestedMessage(bytes);
                break;
            case HAVE:
                message = new HaveMessage(bytes);
                break;
            case BITFIELD:
                message = new BitFieldMessage(bytes);
                break;
            case REQUEST:
                message = new RequestMessage(bytes);
                break;
            case PIECE:
                message = new PieceMessage(bytes);
                break;
            case CANCEL:
                message = new CancelMessage(bytes);
                break;
            case PORT:
                message = new PortMessage(bytes);
                break;
            case KEEPALIVE:
                message = new KeepAliveMessage();
                break;
            default:
                throw new CannotReadMessageException("Impossible to decode the message type " + messageType);
        }

        Logger.getLogger(MessageFactory.class.getName()).log(Level.INFO, "Received a message of type " + messageType);

        return message;
    }

}
