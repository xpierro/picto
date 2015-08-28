package com.github.picto.bencode.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Pierre on 24/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BEncodeInteger {
    String name();
}
