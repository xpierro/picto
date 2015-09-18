package com.github.picto.filesystem;

import java.util.Map;

/**
 * A filesystem meta-info representation.
 * Concerned about: list of files, pieces and checksum, nothing else.
 *
 * One metainfo handles one context for a specific bitorrent meta-info.
 *
 * Created by Pierre on 18/09/15.
 */
public interface FilesystemMetainfo {

    /**
     * A map of file names=>file paths. Path are relative to the torrent context.
     * @return
     */
    Map<String, String> getFilePaths();

    void addFilePath(String fileName, String filePath);

    /**
     * Return the size of torrent pieces.
     * @return
     */
    long getPieceSize();

    void setPieceSize(long pieceSize);

    /**
     * Returns the hash of a particular piece
     * TODO: support MD5 option
     * @return
     */
    byte[] getPieceHash(int pieceIndex);

}
