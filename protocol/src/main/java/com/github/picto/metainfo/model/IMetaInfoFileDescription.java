package com.github.picto.metainfo.model;

import java.util.Optional;

/**
 * Describes a file to save on disk.
 *
 * Created by Pierre on 25/08/15.
 */
public interface IMetaInfoFileDescription {
    /**
     * The path to the file on disk.
     * TODO: path are stored as an array of folder name, should we mimic ?
     * @return A relative path to the root of the user-defined storage.
     */
    String getPath();

    void setPath(final String filePath);

    /**
     * The length of the file in bytes.
     * @return
     */
    int getLength();

    void setLength(final int fileLength);

    /**
     * An optional attribute helping calculate the file integrity.
     * @return A HEX String ASCII encoded.
     */
    Optional<String> getChecksum();

    void setChecksum(String fileChecksum);
}
