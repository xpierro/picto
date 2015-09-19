package com.github.picto.network;

import com.github.picto.network.pwp.PeerWire;
import com.github.picto.network.pwp.TcpListener;

/**
 * Created by Pierre on 02/09/15.
 */
public class TcpListenerTest {

    //@Test
    public void shouldTcpListenerDoSomething() throws InterruptedException {
        try {
            TcpListener listener = new TcpListener(65000) {
                @Override
                protected void emitWire(PeerWire peerWire) {
                    System.out.println();
                }
            };
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
