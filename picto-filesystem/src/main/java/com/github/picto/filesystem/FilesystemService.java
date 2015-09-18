package com.github.picto.filesystem;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;

import java.nio.file.Path;
import java.util.ArrayList;
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
            validatePiece(i);
        }
    }

    @Override
    public void validatePiece(int pieceIndex) {
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

        long pieceLength = (long) metainfo.getPieceLength();

        // We first calculate the byte offset from the beginning of the torrent
        long byteOffset = ((long) pieceIndex) * pieceLength;

        // We now lookup through the files to find: the byte offset in the first file containing the piece, the list of
        // intermediary files containing the piece and the byte offset (included) ending the piece for the last file.
        // If only one file contains the piece the list will have only one file and the start and end offset will
        // reference position inside it.
        List<FileInformation> containingFiles = new ArrayList<>();


        return new byte[0];
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
