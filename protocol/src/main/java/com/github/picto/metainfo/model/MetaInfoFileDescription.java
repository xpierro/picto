package com.github.picto.metainfo.model;

import java.util.Optional;

/**
 * Implementation of the IMetaInfoFileDescription interface.
 *
 * Created by Pierre on 25/08/15.
 */
public class MetaInfoFileDescription implements IMetaInfoFileDescription {
    protected String path;

    protected int length;

    protected String checksum;

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String filePath) {
        this.path = filePath;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLength(int fileLength) {
        this.length = fileLength;
    }

    @Override
    public Optional<String> getChecksum() {
        return Optional.of(checksum);
    }

    @Override
    public void setChecksum(String fileChecksum) {
        this.checksum = fileChecksum;
    }
}
