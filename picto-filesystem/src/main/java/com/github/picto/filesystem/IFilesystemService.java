package com.github.picto.filesystem;

import java.nio.file.Path;
import java.util.List;

/**
 * This interface provides a common utility to handle all filesystem-related needs of a bitorrent client (in the picto
 * meaning: one client = one metainfo file)
 * Created by Pierre on 18/09/15.
 */
public interface IFilesystemService {

    /**
     * Returns the current metainfo descriptor.
     * @return
     */
    FilesystemMetainfo getFilesystemMetainfo();

    /**
     * Validates all pieces and sends event when pieces are validated or invalidated.
     */
    void validateAllPieces();

    void validatePiece(int pieceIndex, byte[] pieceContent);

    /**
     * Create all the files on the physical layer if they don't exist yet.
     */
    void initializeFilesystem();

    void savePiece(final int pieceIndex, final byte[] pieceContent);

    byte[] loadPiece(int pieceIndex);

    List<FileInformation> getAllFileInformations();

    /**
     * Sets the base path holding all the files of the meta info on the filesystem.
     * @param path
     */
    void setFilesystemPath(Path path);

    /**
     * Each time a filesystem event occurs, subscribers will be noticed. Use an underlying Guava EventBus.
     */
    void subscribe(Object subscriber);
}
