package com.github.picto.httputil.stringify;

/**
 * Created by Pierre on 27/08/15.
 */
public class BooleanStringifier extends DefaultStringifier {
    @Override
    public String apply(Object o) {
        if (! (o instanceof Boolean)) {
            throw new IllegalStateException("Impossible to stringify a non-boolean");
        }
        Boolean value = (Boolean) o;
        return value ? "1" : "0";
    }
}
