package com.github.picto.bencode;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the bencoding token defining the type of object a bencoded string represents
 * Created by Pierre on 24/08/15.
 */
public enum BEncodeTypeToken {
    STRING_START('\0'),
    INTEGER_START('i'),
    LIST_START('l'),
    DICTIONARY_START('d'),
    END('e');

    private final static Map<Character, BEncodeTypeToken> tokenMap;

    static {
        tokenMap = new HashMap<>();
        for (BEncodeTypeToken token : values()) {
            tokenMap.put(token.token, token);
        }
    }


    private final char token;

    private BEncodeTypeToken(final char token) {
        this.token = token;
    }

    public char getToken() {
        return token;
    }


    public static BEncodeTypeToken findToken(final char tokenBegin) {
        return tokenMap.containsKey(tokenBegin) ? tokenMap.get(tokenBegin) : STRING_START;
    }
}
