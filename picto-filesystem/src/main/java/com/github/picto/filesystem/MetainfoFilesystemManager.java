package com.github.picto.filesystem;

import java.util.List;

/**
 * This interface provides a common utility to handle all filesystem-related needs of a bitorrent client (in the picto
 * meaning: one client = one metainfo file)
 * Created by Pierre on 18/09/15.
 */
public interface MetainfoFilesystemManager {

    /**
     * Returns the current metainfo descriptor.
     * @return
     */
    FilesystemMetainfo getFilesystemMetainfo();

    void validateAllPieces();

    void validatePiece(int pieceIndex);

    void savePiece(final int pieceIndex, final byte[] pieceContent);

    List<FileInformation> getAllFileInformations();

    /**
     * Each time a filesystem event occurs, subscribers will be noticed. Use an underlying Guava EventBus.
     */
    void subscribe(Object subscriber);
}
