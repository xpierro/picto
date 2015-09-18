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

    private int startBlockOffset;

    private int endPieceIndex;

    private int endPieceOffset;

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

    public void setStartPieceIndex(int startPieceIndex) {
        this.startPieceIndex = startPieceIndex;
    }

    public int getStartBlockOffset() {
        return startBlockOffset;
    }

    public void setStartBlockOffset(int startBlockOffset) {
        this.startBlockOffset = startBlockOffset;
    }

    public int getEndPieceIndex() {
        return endPieceIndex;
    }

    public void setEndPieceIndex(int endPieceIndex) {
        this.endPieceIndex = endPieceIndex;
    }

    public int getEndPieceOffset() {
        return endPieceOffset;
    }

    public void setEndPieceOffset(int endPieceOffset) {
        this.endPieceOffset = endPieceOffset;
    }
}
