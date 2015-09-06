package com.github.picto.network.pwp;


import com.github.picto.network.pwp.message.Message;
import io.netty.channel.socket.SocketChannel;

import java.util.Observable;

/**
 * Peer connection, allowing to receive and send message to a peer.
 *
 * Created by Pierre on 03/09/15.
 */
public class PeerWire extends Observable {

    private SocketChannel socketChannel;

    public PeerWire(final SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void emitMessage(final Message message) {

        this.notifyObservers(message);
    }

    public void sendMessage(final Message message) {
        byte[] bytes = message.getRawBytes();
        try {
            // TODO: do we need to synchronize here
            socketChannel.writeAndFlush(bytes).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        String ip = socketChannel.remoteAddress().getHostString();
        int port = socketChannel.remoteAddress().getPort();

        return ip + ":" + port;
    }

}
