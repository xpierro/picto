package com.github.picto.network.pwp.annotation;

/**
 * Created by Pierre on 05/09/15.
 */
public @interface PwpMessageFragment {
    /**
     * A -1 length means it's dynamic, context dependent
     */
    int length() default -1;
    int order();
}
