package com.github.picto.httputil.serialization;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pierre on 28/08/15.
 */
public class ExtendedURIBuilder extends URIBuilder {
    private static final String TOKEN = "TOKENTOREPLACE";

    private final List<String> rawValues;

    public ExtendedURIBuilder() {
        super();
        rawValues = new ArrayList<>();
    }

    public void addRawParameter(String name, String rawValue) {
        // we add a token to replace in the url later, it must be escaped already
        addParameter(name, "TOKENTOREPLACE" + rawValues.size());

        rawValues.add(rawValue);
    }

    @Override
    public URI build() throws URISyntaxException {
        URI uri = super.build();
        String uriString = uri.toString();

        for (int i = 0; i < rawValues.size(); i++) {
            uriString = uriString.replace(TOKEN + i, rawValues.get(i));
        }
        return new URI(uriString);
    }

    /**
     * In case we have a pre-escaped parameter, we insert it as is.
     * @param name The name of the parameter
     * @param value The value of the parameter
     * @param raw True if the parameter should be inserted as is, false otherwise.
     */
    public void addParameter(String name, String value, boolean raw) {
        if (raw) {
            addRawParameter(name, value);
        } else {
            addParameter(name, value);
        }
    }
}
