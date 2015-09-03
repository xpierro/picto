package com.github.picto.network.http.annotation;

import com.github.picto.network.http.stringify.DefaultStringifier;
import com.github.picto.network.http.stringify.Stringifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Pierre on 27/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface URLEncode {

    String name();
    Class<? extends Stringifier> stringify() default DefaultStringifier.class;
    boolean raw() default false;
}
