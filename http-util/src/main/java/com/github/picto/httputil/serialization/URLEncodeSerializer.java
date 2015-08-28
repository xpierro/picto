package com.github.picto.httputil.serialization;

import com.github.picto.httputil.annotation.GET;
import com.github.picto.httputil.annotation.URLEncode;
import com.github.picto.httputil.exception.URLEncodeException;
import com.github.picto.httputil.stringify.Stringifier;
import org.apache.http.client.utils.URIBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Serialize a POJO into an URL encoded key=>value string.
 * Created by Pierre on 27/08/15.
 */
public class URLEncodeSerializer {

    private final Object source;

    private final Map<Method, URLEncode> methodURLEncodeMap = new HashMap<>();

    public URLEncodeSerializer(final Object source) {
        this.source = source;
        buildMap();
    }

    private void buildMap() {
        // We browse the class looking for @URLEncode getters
        for (final Method method : source.getClass().getMethods()) {
            if (method.isAnnotationPresent(URLEncode.class)) {
                methodURLEncodeMap.put(method, method.getAnnotation(URLEncode.class));
            }
        }
    }

    /**
     * Serialize the provided class into a URIBuilder prepared for an HTTP POST or GET query.
     * @return An incomplete URIBuilder with the relevant parameter fragments
     */
    public URIBuilder serializeGet() throws URLEncodeException {
        if (!source.getClass().isAnnotationPresent(GET.class)) {
            throw new IllegalStateException("Impossible to serialize this POJO to a get request.");
        }

        final ExtendedURIBuilder uriBuilder = new ExtendedURIBuilder();
        // For each method, we send to the builder the key and value.
        for (final Method method : methodURLEncodeMap.keySet()) {
            final URLEncode urlEncode = methodURLEncodeMap.get(method);

            try {
                Object value = method.invoke(source);

                if (value != null) {

                    String parameterName = urlEncode.name();

                    Stringifier valueStringifier = urlEncode.stringify().newInstance();
                    String parameterValue = valueStringifier.apply(value);

                    uriBuilder.addParameter(parameterName, parameterValue, urlEncode.raw());
                }

            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new URLEncodeException("Impossible to encode this value into an URL parameter.", e);
            }


        }
        return uriBuilder;
    }

}
