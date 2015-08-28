package com.github.picto.bencode.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation indicating a bencodeable dictionary.
 * Created by Pierre on 25/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BEncodeDictionary {
    String name() default "";
    Class<?> type();
}
