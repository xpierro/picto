package com.github.picto.bencode;

import com.github.picto.bencode.type.BEncodeableDictionary;

/**
 * Represents a bencoded dictionary POJO
 * Created by Pierre on 28/08/15.
 */
public interface BEncodedDictionary {

    /**
     * Returns the bencoded dictionary for the current instance.
     * @return A BEncodeableDictionary that was used to represent this object.
     */
    BEncodeableDictionary getBEncodeableDictionary();

    /**
     * Associates a bencoded dictionary to the current instance.
     * @param dictionary A bencoded dictionary to associate to the current instance.
     */
    void setBEncodeableDictionary(final BEncodeableDictionary dictionary);
}
