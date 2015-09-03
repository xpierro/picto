package com.github.picto.network.http;

import com.github.picto.network.http.exception.URLEncodeException;
import com.github.picto.network.http.serialization.URLEncodeSerializer;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pierre on 28/08/15.
 */
public class RequestBuilder {

    public URI buildGetUri(final Object requestParameters, final String announcePath) throws URLEncodeException, URISyntaxException {
        URLEncodeSerializer urlEncodeSerializer = new URLEncodeSerializer(requestParameters);
        URIBuilder uriBuilder = urlEncodeSerializer.serializeGet();

        buildAnnounceUri(uriBuilder, announcePath);

        uriBuilder.setCharset(Charset.forName("UTF-8"));
        uriBuilder.setScheme("http");

        return uriBuilder.build();
    }

    private void buildAnnounceUri(final URIBuilder uriBuilder, final String announcePath) {
        String pathRegex = "([^:]*://)*([^:]*):([1-9]*)(/.*)";
        Pattern pathPattern = Pattern.compile(pathRegex);
        Matcher matcher = pathPattern.matcher(announcePath);

        if (matcher.find()) {

            uriBuilder.setHost(matcher.group(2));
            uriBuilder.setPort(Integer.parseInt(matcher.group(3)));
            uriBuilder.setPath(matcher.group(4));
        }

    }
}
