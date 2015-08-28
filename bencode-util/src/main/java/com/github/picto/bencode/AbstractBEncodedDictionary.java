package com.github.picto.bencode;

import com.github.picto.bencode.type.BEncodeableDictionary;

/**
 * Created by Pierre on 28/08/15.
 */
public abstract class AbstractBEncodedDictionary implements BEncodedDictionary {
    protected BEncodeableDictionary dictionary;

    @Override
    public BEncodeableDictionary getBEncodeableDictionary() {
        return this.dictionary;
    }

    @Override
    public void setBEncodeableDictionary(BEncodeableDictionary dictionary) {
        this.dictionary = dictionary;
    }
}
