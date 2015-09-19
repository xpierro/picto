package com.github.picto.filesystem;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by Pierre on 19/09/15.
 */
public class FilesystemService implements IFilesystemService {

    @Inject
    private FilesystemMetainfo metainfo;

    private ListeningExecutorService executorService;

    public FilesystemService() {
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    }

    @Override
    public FilesystemMetainfo getFilesystemMetainfo() {
        return metainfo;
    }

    @Override
    public void validateAllPieces() {
        for (int i = 0; i < metainfo.getPieceCount(); i++) {
            validatePiece(i, loadPiece(i));
        }
    }

    @Override
    public void validatePiece(int pieceIndex, byte[] pieceContent) {
        final byte[] expectedHash = metainfo.getPieceHash(pieceIndex);

        executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return null;
            }
        });
    }

    @Override
    public void savePiece(int pieceIndex, byte[] pieceContent) {

    }

    // TODO: make it asynchronous
    @Override
    public byte[] loadPiece(int pieceIndex) {
        // Loading a piece from the filesystem is particularly complex.
        // A piece can span multiple files, and we therefore need to find where the first block is, and then parse
        // through all the next files.

        int pieceLength = metainfo.getPieceLength();

        // We first calculate the byte offset from the beginning of the torrent
        long byteOffset = (long) pieceIndex * (long) pieceLength;

        // We now lookup through the files to find: the byte offset in the first file containing the piece, the list of
        // intermediary files containing the piece and the byte offset (included) ending the piece for the last file.
        // If only one file contains the piece the list will have only one file and the start and end offset will
        // reference position inside it.
        List<FileInformation> containingFiles = metainfo.getOrderedFilesContained(pieceIndex);
        long relativeByteOffset = byteOffset - containingFiles.get(0).getByteOffset();
        long sizeRead = 0;

        byte[] pieceContent = new byte[pieceLength];

        for (FileInformation fileInformation : containingFiles) {
            int sizeToRead = (int) Math.min(pieceLength - sizeRead, fileInformation.getFileSize());

            ByteBuffer dst = ByteBuffer.allocate(sizeToRead);
            readPieceChunck(fileInformation.getFilePath(), relativeByteOffset, sizeToRead, dst, pieceContent, (int) sizeRead);
            sizeRead += sizeToRead;
            relativeByteOffset = 0; // For next files, we will read from the beginning.

        }

        return pieceContent;
    }

    void readPieceChunck(Path filePath, long position, int sizeToRead, ByteBuffer dst, byte[] pieceContent, int pieceContentOffset) {
        try(SeekableByteChannel sbc = Files.newByteChannel(filePath)) {
            sbc.position(position);
            sbc.read(dst);
            // Now that we have the content:
            System.arraycopy(dst.array(), 0, pieceContent, pieceContentOffset, sizeToRead);

        } catch (IOException e) {
            // TODO: what to do here
            e.printStackTrace();
        }
    }

    @Override
    public List<FileInformation> getAllFileInformations() {
        return null;
    }

    @Override
    public void setFilesystemPath(Path path) {

    }

    @Override
    public void subscribe(Object subscriber) {

    }
}
