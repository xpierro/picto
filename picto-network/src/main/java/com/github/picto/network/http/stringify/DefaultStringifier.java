package com.github.picto.network.http.stringify;

/**
 * Created by Pierre on 27/08/15.
 */
public class DefaultStringifier implements Stringifier {
    @Override
    public String apply(Object o) {
        return o.toString();
    }
}
