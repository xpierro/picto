package com.github.picto.filesystem;

import com.github.picto.filesystem.event.FilesystemReadyEvent;
import com.github.picto.filesystem.event.PieceValidationEvent;
import com.github.picto.util.Hasher;
import com.github.picto.util.exception.HashException;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pierre on 19/09/15.
 */
public class FilesystemService implements IFilesystemService {

    /**
     * Size of a block of bytes we want to insert during initialization. It's done to avoid using too much memory,
     * during file initialzation.
     */
    private static final long MAX_BLOCK_SIZE = 32 * 1024;

    @Inject
    private IFilesystemMetainfo metainfo;

    @Inject
    private EventBus eventBus;

    private ListeningExecutorService executorService;

    public FilesystemService() {
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    }

    @Override
    public IFilesystemMetainfo getFilesystemMetainfo() {
        return metainfo;
    }

    @Override
    public void validateAllPieces() throws HashException {
        for (int i = 0; i < metainfo.getPieceCount(); i++) {
            if(validatePiece(i, loadPiece(i))) {
                eventBus.post(PieceValidationEvent.valid(i));
            } else {
                eventBus.post(PieceValidationEvent.invalid(i));
            }
        }
    }

    /**
     * Synchronous method validating a piece.
     * @return True if the piece has the expected Hash
     */
    public boolean validatePiece(int pieceIndex, byte[] pieceContent) throws HashException {
        byte[] expectedPieceHash = metainfo.getPieceHash(pieceIndex);
        byte[] actualPieceHash = Hasher.sha1(pieceContent);

        return Arrays.equals(expectedPieceHash, actualPieceHash);
    }

    @Override
    public void validateAndSavePiece(int pieceIndex, byte[] pieceContent) {
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        });
    }

    @Override
    public void initializeFilesystem() {
        if (metainfo.getBasePath() == null || !Files.isDirectory(metainfo.getBasePath())) {
            throw new IllegalStateException("The torrent base path isn't defined");
        }
        // We need to create, in advance, the whole torrent file system. Doing so is slow for big torrent and must
        // be done asynchronously.
        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // For each file we need to check if it exists, then allocate it on the filesystem and update the physical
                // path of the fileinfo.
                for (FileInformation fileInformation : metainfo.getFileInformations()) {
                    String abstractRelativePath = fileInformation.getAbstractFilePath();
                    Path torrentBasePath = metainfo.getBasePath();

                    Path filePhysicalPath = torrentBasePath.resolve(abstractRelativePath);
                    if (Files.isDirectory(filePhysicalPath)) {
                        throw new IllegalStateException("A directory with the same path already exists.");
                    }

                    // If the file already exists but has wrong size we delete it.
                    if(Files.exists(filePhysicalPath)) {
                        if (!(Files.size(filePhysicalPath) == fileInformation.getFileSize())) {
                            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "The file " + filePhysicalPath + " already exists with a wrong size, erasing...");
                            Files.delete(filePhysicalPath);
                            initializeFile(filePhysicalPath, fileInformation.getFileSize());
                        } else {
                            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "The file " + filePhysicalPath + " already exists, ignoring...");
                        }
                    } else {
                        initializeFile(filePhysicalPath, fileInformation.getFileSize());
                    }
                }

                eventBus.post(new FilesystemReadyEvent());

                return null;
            }
        });
    }

    /**
     * Create the file on the filesystem. Already existing files will be overwritten.
     * The whole file will be written with zeroes.
     */
    void initializeFile(Path filePath, long fileSize) throws IOException {
        Files.createDirectories(filePath.getParent());

        // We fill the file with zeros
        try(SeekableByteChannel sbc = Files.newByteChannel(filePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            long sizeWritten = 0;

            while (sizeWritten < fileSize) {
                int sizeToWrite = (int) Math.min(fileSize - sizeWritten, MAX_BLOCK_SIZE);

                ByteBuffer src = ByteBuffer.allocate(sizeToWrite);

                if(sbc.write(src) != sizeToWrite) {
                    //TODO: maybe less hardcore exception, a problem could appear.
                    throw new IllegalStateException("The piece content cannot be written.");
                }
                sizeWritten += sizeToWrite;
            }

        } catch (IOException e) {
            // TODO: what to do here
            e.printStackTrace();
        }
    }

    // TODO: make it asynchronous with AsynchronousFileChannel
    @Override
    public void savePiece(int pieceIndex, byte[] pieceContent) {
        // We suppose the filesystem has been initialized beforehand, and we need to writer the real bytes on it.
        // To do that, we first need to get where to write: it works like the piece loading, except a write
        // operation will be performed.
        int pieceLength = metainfo.getPieceLength();

        // We first calculate the byte offset from the beginning of the torrent
        long byteOffset = (long) pieceIndex * (long) pieceLength;

        // We now lookup through the files to find: the byte offset in the first file containing the piece, the list of
        // intermediary files containing the piece and the byte offset (included) ending the piece for the last file.
        // If only one file contains the piece the list will have only one file and the start and end offset will
        // reference position inside it.
        List<FileInformation> containingFiles = metainfo.getOrderedFilesContained(pieceIndex);
        long relativeByteOffset = byteOffset - containingFiles.get(0).getByteOffset();
        long sizeWritten = 0;

        for (FileInformation fileInformation : containingFiles) {
            int sizeToWrite = (int) Math.min(pieceLength - sizeWritten, fileInformation.getFileSize());

            writePieceChunck(fileInformation.getFilePath(), relativeByteOffset, sizeToWrite, pieceContent, (int) sizeWritten);

            sizeWritten += sizeToWrite;
            relativeByteOffset = 0; // For next files, we will read from the beginning.
        }

    }

    // TODO: make it asynchronous with AsynchronousFileChannel
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

            readPieceChunck(fileInformation.getFilePath(), relativeByteOffset, sizeToRead, pieceContent, (int) sizeRead);
            sizeRead += sizeToRead;
            relativeByteOffset = 0; // For next files, we will read from the beginning.

        }

        return pieceContent;
    }

    void writePieceChunck(Path filePath, long filePosition, int sizeToWrite, byte[] pieceContent, int pieceContentOffset) {
        try(SeekableByteChannel sbc = Files.newByteChannel(filePath, StandardOpenOption.WRITE)) {
            sbc.position(filePosition);

            ByteBuffer src = ByteBuffer.wrap(pieceContent, pieceContentOffset, sizeToWrite);

            if(sbc.write(src) != sizeToWrite) {
                //TODO: maybe less hardcore exception, a problem could appear.
                throw new IllegalStateException("The piece content cannot be written.");
            }

        } catch (IOException e) {
            // TODO: what to do here
            e.printStackTrace();
        }
    }

    void readPieceChunck(Path filePath, long filePosition, int sizeToRead, byte[] pieceContent, int pieceContentOffset) {
        try(SeekableByteChannel sbc = Files.newByteChannel(filePath)) {
            sbc.position(filePosition);

            ByteBuffer dst = ByteBuffer.allocate(sizeToRead);
            if(sbc.read(dst) != sizeToRead) {
                throw new IllegalStateException("The piece content cannot be read.");
            }
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
        eventBus.register(subscriber);
    }
}
