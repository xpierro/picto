package com.github.picto.network;

import com.github.picto.network.pwp.exception.CannotReadMessageException;
import com.github.picto.network.pwp.message.RequestMessage;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pierre on 17/09/15.
 */
public class MessageTest {

    @Test
    public void requestMessageTest() throws CannotReadMessageException {
        int pieceIndex = 54;
        int blockOffset = 32;
        int length = 16384;

        RequestMessage explodedRequest = new RequestMessage(pieceIndex, blockOffset, length);
        RequestMessage compactRequest = new RequestMessage(Arrays.copyOfRange(explodedRequest.getRawBytes(), 5, explodedRequest.getRawBytes().length));

        assertEquals(pieceIndex, compactRequest.getPieceIndex());
        assertEquals(blockOffset, compactRequest.getBlockOffset());
        assertEquals(length, compactRequest.getLength());
    }
}
