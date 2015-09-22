package com.github.picto.filesystem;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Pierre on 20/09/15.
 */
public interface IFilesystemMetainfo {

    /**
     * A map of file names=>file paths. Path are relative to the torrent context.
     * @return
     */
    List<FileInformation> getFileInformations();

    /**
     * Adds a file information in the right order. Files in bitorrent ARE ordered and this must stay consistent.
     * @param fileInformation
     */
    void addFileInformation(FileInformation fileInformation);

    void addFileInformation(String fileName, String abstractFilePath, long fileSize);

    /**
     * For large file, implementing with a simple map takes a lot of memory. It is recommended to recalculate each time.
     * @param pieceIndex
     * @return
     */
    List<FileInformation> getOrderedFilesContained(int pieceIndex);

    /**
     * Return the size of torrent pieces in bytes.
     * @return
     */
    public int getPieceLength();

    public void setPieceLength(int pieceLength);

    /**
     * Returns the hash of a particular piece
     * TODO: support MD5 option
     * @return
     */
    public byte[] getPieceHash(int pieceIndex);

    public void setPieceHash(int pieceIndex, byte[] hash);

    public int getPieceCount();

    public void setPieceCount(int pieceCount);

    public Path getBasePath();

    public void setBasePath(Path path);
}
