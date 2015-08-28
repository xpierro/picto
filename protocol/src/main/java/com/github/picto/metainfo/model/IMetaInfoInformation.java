package com.github.picto.metainfo.model;

import com.github.picto.bencode.BEncodedDictionary;

import java.util.List;

/**
 * Defines the set of informations regarding a meta-info file.
 * Created by Pierre on 25/08/15.
 */
public interface IMetaInfoInformation extends BEncodedDictionary {
    /**
     * The number of bytes in each piece.
     *
     * The piece length specifies the nominal piece size, and is usually a power of 2. The piece size is typically
     * chosen based on the total amount of file data in the
     * torrent, and is constrained by the fact that too-large piece sizes cause inefficiency,
     * and too-small piece sizes cause large .torrent metadata file. Historically, piece
     * size was chosen to result in a .torrent file no greater than approx. 50 - 75 kB
     * (presumably to ease the load on the server hosting the torrent files).
     * Current best-practice is to keep the piece size to 512KB or less, for torrents around 8-10GB,
     * even if that results in a larger .torrent file. This results in a
     * more efficient swarm for sharing files. The most common sizes are 256 kB, 512 kB, and 1 MB.
     * Every piece is of equal length except for the final piece, which is irregular. The number of pieces is thus
     * determined by 'ceil( total length / piece size )'.
     * For the purposes of piece boundaries in the multi-file case, consider the file data as one long continuous
     * stream, composed of the concatenation of each file
     * in the order listed in the files list. The number of pieces and their boundaries are then determined in
     * the same manner as the case of a single file. Pieces
     * may overlap file boundaries.
     *
     * @return An integer value representing the byte count of each piece.
     */
    int getPieceLength();

    void setPieceLength(final int pieceLength);

    /**
     * Gets the hash of a particlar piece.
     * @return A byte array of 20 byte representing a SHA1 piece hash.
     */
    byte[] getPieceHash(final int pieceIndex);

    void setPieceHash(final int pieceIndex, final byte[] pieceHash);

    /**
     * Indicates if a torrent is from a private tracker.
     * @return True if the torrent must block all out-of-tracker peer exchange.
     */
    boolean isPrivate();

    void setPrivate(final boolean privateTracker);

    /**
     * Informative name of the root of the file structure. Can define a folder or filename.
     * @return A UTF-8 file name that helps the user decide how to store the files. Purely indicative.
     */
    String getRootName();

    void setRootName(final String rootName);

    /**
     * Indicates if the meta info defines a group of files under a folder or a unique file.
     * @return True if the meta info is for multi file storage.
     */
    boolean isMultifiles();

    void setMultiFile(final boolean multiFile);

    /**
     * Returns all the file that will be stored in the MetaInfo file.
     * In case of mono-file storage, the name of the file can be ignored.
     * @return A list of 1 or more file descriptions.
     */
    List<IMetaInfoFileDescription> getFiles();

    void setFiles(final List<IMetaInfoFileDescription> files);

}
