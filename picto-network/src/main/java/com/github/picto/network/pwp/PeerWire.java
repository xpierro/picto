package com.github.picto.network.pwp;


import com.github.picto.network.pwp.message.Message;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Observable;

/**
 * Peer connection, allowing to receive and send message to a peer.
 *
 * Created by Pierre on 03/09/15.
 */
public class PeerWire extends Observable {

    private Channel channel;

    public PeerWire(final Channel channel) {
        this.channel = channel;
    }

    public void emitMessage(final Message message) {

        this.notifyObservers(message);
    }

    public void sendMessage(final Message message) {
        byte[] bytes = message.getRawBytes();
        try {
            // TODO: do we need to synchronize here
            channel.writeAndFlush(bytes).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        String ip = getHost().getHostAddress();
        int port = getPort();

        return ip + ":" + port;
    }

    public InetAddress getHost() {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress();
    }

    public int getPort() {
        return ((InetSocketAddress) channel.remoteAddress()).getPort();
    }

}
