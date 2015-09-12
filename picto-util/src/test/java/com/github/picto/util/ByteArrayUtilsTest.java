package com.github.picto.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pierre on 13/09/15.
 */
public class ByteArrayUtilsTest {

    @Test
    public void intByteArrayConversionShouldBeConsistent() {
        assertEquals(42, ByteArrayUtils.byteArrayToInteger(ByteArrayUtils.integerToByteArray(42)));
    }
}
