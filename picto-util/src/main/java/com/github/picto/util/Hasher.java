package com.github.picto.util;

import com.github.picto.util.exception.HashException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class providing hashing capabilities
 * Created by Pierre on 29/08/15.
 */
public class Hasher {

    public static byte[] sha1(final byte[] bytes) throws HashException {
        try {
            return MessageDigest.getInstance("SHA-1").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new HashException("Impossible to hash this byte array to a SHA1 byte array.");
        }
    }
}
