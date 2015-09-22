package com.github.picto.filesystem;

import com.google.common.collect.Range;

import java.nio.file.Path;
import java.util.*;

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
public class FilesystemMetainfo implements IFilesystemMetainfo {

    private List<FileInformation> fileInformations;

    private Map<Integer, byte[]> pieceHashes;

    private Path basePath;

    private int pieceLength;
    private int pieceCount;

    private int cumulativeSize;

    private LinkedHashMap<Range<Integer>, List<FileInformation>> piecesToFileIndexRangeMap;

    public FilesystemMetainfo(List<FileInformation> fileInformations) {
        this.fileInformations = fileInformations;
        pieceHashes = new HashMap<>();

        piecesToFileIndexRangeMap = new LinkedHashMap<>();

        cumulativeSize = 0;
    }

    public FilesystemMetainfo() {
        this(new ArrayList<>());
    }

    /**
     * A map of file names=>file paths. Path are relative to the torrent context.
     * @return
     */
    public List<FileInformation> getFileInformations() {
        return fileInformations;
    }

    /**
     * Adds a file information in the right order. Files in bitorrent ARE ordered and this must stay consistent.
     * @param fileInformation
     */
    public void addFileInformation(FileInformation fileInformation) {
        if(pieceLength <= 0) {
            throw new IllegalStateException("Impossible to have a negative or null piece length.");
        }

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

        fileInformation.setByteOffset(cumulativeSize);
        fileInformations.add(fileInformation);
        addToFilesForPiece(fileInformation);
        cumulativeSize += fileInformation.getFileSize();
    }

    @Override
    public void addFileInformation(String fileName, String abstractFilePath, long fileSize) {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setFileName(fileName);
        fileInformation.setAbstractFilePath(abstractFilePath);
        fileInformation.setFileSize(fileSize);

        addFileInformation(fileInformation);
    }

    private void addToFilesForPiece(FileInformation fileInformation) {

        Range<Integer> range = Range.closed(fileInformation.getStartPieceIndex(), fileInformation.getEndPieceIndex());
        if (!piecesToFileIndexRangeMap.containsKey(range)) {
            piecesToFileIndexRangeMap.put(range, new ArrayList<>());
        }

        piecesToFileIndexRangeMap.get(range).add(fileInformation);
    }

    public List<FileInformation> getOrderedFilesContained(int pieceIndex) {
        List<FileInformation> files = new ArrayList<>();
        // We assume the linked hash map provides the key in insertion order (ascending range)
        for (Range<Integer> range : piecesToFileIndexRangeMap.keySet()) {
            if (range.contains(pieceIndex)) {
                files.addAll(piecesToFileIndexRangeMap.get(range));
            }
        }
        return files;

    }

    /**
     * Return the size of torrent pieces in bytes.
     * @return
     */
    public int getPieceLength() {
        return pieceLength;
    }

    public void setPieceLength(int pieceLength) {
        this.pieceLength = pieceLength;
    }

    /**
     * Returns the hash of a particular piece
     * TODO: support MD5 option
     * @return
     */
    public byte[] getPieceHash(int pieceIndex) {
        return this.pieceHashes.get(pieceIndex);
    }

    public void setPieceHash(int pieceIndex, byte[] hash) {
        this.pieceHashes.put(pieceIndex, hash);
    }

    public int getPieceCount() {
        return pieceCount;
    }

    public void setPieceCount(int pieceCount) {
        this.pieceCount = pieceCount;
    }

    @Override
    public Path getBasePath() {
        return basePath;
    }

    @Override
    public void setBasePath(Path path) {
        this.basePath = path;
    }
}
