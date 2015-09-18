package com.github.picto.filesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A filesystem meta-info representation.
 * Concerned about: list of files, pieces and checksum, nothing else.
 *
 * One metainfo handles one context for a specific bitorrent meta-info.
 *
 * The metainfo isn't concerned about physical representation of the file and can hold only virtual files.
 *
 * Created by Pierre on 18/09/15.
 */
public class FilesystemMetainfo {

    private List<FileInformation> fileInformations;

    private Map<Integer, byte[]> pieceHashes;

    private int pieceLength;
    private int pieceCount;

    private int cumulativeSize;

    public FilesystemMetainfo(List<FileInformation> fileInformations) {
        this.fileInformations = fileInformations;
        pieceHashes = new HashMap<>();

        cumulativeSize = 0;
    }

    public FilesystemMetainfo() {
        this(new ArrayList<>());
    }

    /**
     * A map of file names=>file paths. Path are relative to the torrent context.
     * @return
     */
    List<FileInformation> getFileInformations() {
        return fileInformations;
    }

    /**
     * Adds a file information in the right order. Files in bitorrent ARE ordered and this must stay consistent.
     * @param fileInformation
     */
    void addFileInformation(FileInformation fileInformation) {
        // We calculate first and last piece index for the newly inserted file
        if (fileInformations.size() == 0) { // We are inserting the first file
            fileInformation.setStartPieceIndex(0);
        } else {
            FileInformation lastInsertedFile = fileInformations.get(fileInformations.size() - 1);
            if (cumulativeSize % pieceLength == 0) {
                fileInformation.setStartPieceIndex(lastInsertedFile.getEndPieceIndex() + 1);
            } else {
                fileInformation.setStartPieceIndex(lastInsertedFile.getEndPieceIndex());
            }
        }

        int lastFileMissingBytesForPiece = pieceLength - (cumulativeSize % pieceLength);
        if (fileInformation.getFileSize() < lastFileMissingBytesForPiece) {
            fileInformation.setEndPieceIndex(fileInformation.getStartPieceIndex());
        } else {
            long nextSize = cumulativeSize + fileInformation.getFileSize();
            int numberOfPiecesNextSize = ((int) (nextSize / pieceLength) + (nextSize % pieceLength == 0 ? 0 : 1));

            fileInformation.setEndPieceIndex(numberOfPiecesNextSize);
        }

        fileInformations.add(fileInformation);
        cumulativeSize += fileInformation.getFileSize();

    }

    /**
     * Return the size of torrent pieces in bytes.
     * @return
     */
    int getPieceLength() {
        return pieceLength;
    }

    void setPieceLength(int pieceLength) {
        this.pieceLength = pieceLength;
    }

    /**
     * Returns the hash of a particular piece
     * TODO: support MD5 option
     * @return
     */
    byte[] getPieceHash(int pieceIndex) {
        return this.pieceHashes.get(pieceIndex);
    }

    void setPieceHash(int pieceIndex, byte[] hash) {
        this.pieceHashes.put(pieceIndex, hash);
    }

    public int getPieceCount() {
        return pieceCount;
    }

    public void setPieceCount(int pieceCount) {
        this.pieceCount = pieceCount;
    }
}
