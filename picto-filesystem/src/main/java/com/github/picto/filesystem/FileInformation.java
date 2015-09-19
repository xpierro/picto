package com.github.picto.filesystem;

import java.nio.file.Path;

/**
 * Stores information about a file.
 * Created by Pierre on 18/09/15.
 */
public class FileInformation {

    private String fileName;

    private Path filePath;

    private String abstractFilePath;

    private long fileSize;

    private long validatedFileSize;

    private int startPieceIndex;

    private int endPieceIndex;

    private long byteOffset;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public String getAbstractFilePath() {
        return abstractFilePath;
    }

    public void setAbstractFilePath(String abstractFilePath) {
        this.abstractFilePath = abstractFilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getValidatedFileSize() {
        return validatedFileSize;
    }

    public void setValidatedFileSize(long validatedFileSize) {
        this.validatedFileSize = validatedFileSize;
    }

    public int getStartPieceIndex() {
        return startPieceIndex;
    }

    /**
     * Can only be set at insertion into the metadata as it depends on the file order.
     * @param startPieceIndex
     */
    public void setStartPieceIndex(int startPieceIndex) {
        this.startPieceIndex = startPieceIndex;
    }

    public int getEndPieceIndex() {
        return endPieceIndex;
    }

    /**
     * Can only be set at insertion into the metadata as it depends on the file order.
     * @param endPieceIndex
     */
    public void setEndPieceIndex(int endPieceIndex) {
        this.endPieceIndex = endPieceIndex;
    }

    /**
     * Returns the global byte offset of the start of that file inside the whole virtual bitorrent filesystem.
     * @return
     */
    public long getByteOffset() {
        return byteOffset;
    }

    public void setByteOffset(long byteOffset) {
        this.byteOffset = byteOffset;
    }
}
