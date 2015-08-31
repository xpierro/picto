package com.github.picto.network.http;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import java.net.URI;
import java.util.function.Function;

/**
 * Created by Pierre on 31/08/15.
 */
public class GetExecutor {
    public void execute(final URI requestURI, Function<byte[], ?> onByteReceived, Function<Void, Void> onConnectionClose) {
        HttpClient.newClient(requestURI.getHost(), requestURI.getPort())
                .createGet(requestURI.getPath() + "?" + requestURI.getRawQuery())
                .flatMap((HttpClientResponse<ByteBuf> resp) ->
                        resp.getContent().map(new Func1<ByteBuf, byte[]>() {
                                                  @Override
                                                  public byte[] call(ByteBuf byteBuf) {
                                                      byte[] content = new byte[byteBuf.capacity()];
                                                      byteBuf.getBytes(0, content);
                                                      return content;
                                                  }
                                              }

                        )).doOnCompleted(new Action0() {
            @Override
            public void call() {
                onConnectionClose.apply(null);
            }
        }).forEach(new Action1<byte[]>() {
                       @Override
                       public void call(byte[] bytes) {
                            onByteReceived.apply(bytes);
                       }
                   }
        );
    }
}
