package com.github.picto.bencode.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates an attribute as a list of BEncodeable objets.
 * Created by Pierre on 24/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BEncodeList {
    String name();

    /**
     * Types of BEncodeable element inside the list.
     * @return The type of elements inside the list.
     */
    Class<?> elementType();

    /**
     * Array representation of inner list tree.
     * @return
     */
    BEncodeInnerList[] innerList() default {};
}
