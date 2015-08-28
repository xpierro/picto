package com.github.picto.thp.request;

import com.github.picto.thp.exception.THPRequestException;

import java.net.URI;

/**
 * Created by Pierre on 29/08/15.
 */
public interface GetRequest {
    URI getUri() throws THPRequestException;
}
