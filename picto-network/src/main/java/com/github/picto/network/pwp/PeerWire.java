package com.github.picto.network.pwp;


import com.github.picto.network.pwp.message.Message;
import com.google.common.eventbus.EventBus;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Peer connection, allowing to receive and send message to a peer.
 *
 * Created by Pierre on 03/09/15.
 */
public class PeerWire {

    private Channel channel;

    private EventBus internalEventBus;

    public PeerWire(final Channel channel) {

        this.channel = channel;
        this.internalEventBus = new EventBus();
    }

    /**
     * Subscribe to the internal event bus to receive messages from that particular wire.
     * @param observer
     */
    public void listenToWire(final Object observer) {
        internalEventBus.register(observer);
    }

    public void onMessageReceived(final Message message) {

        internalEventBus.post(message);
    }

    public void sendMessage(final Message message) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sending a new message of type " + message.getType());
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
