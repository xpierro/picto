package com.github.picto.bencode.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Pierre on 27/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BEncodeInnerList {
    /**
     * Types of BEncodeable element inside the list.
     * @return The type of elements inside the list.
     */
    Class<?> elementType();
}
