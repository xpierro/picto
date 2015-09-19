package com.github.picto.filesystem;

import com.github.picto.module.FilesystemModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.file.Path;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * Created by Pierre on 20/09/15.
 */
public class FilesystemServiceTest {
    private FilesystemService service;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new FilesystemModule());
        service = Mockito.spy(injector.getInstance(FilesystemService.class));
    }

    private FileInformation buildFileInformation(String fileName, long fileSize, String filePathInTorrent) {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setFileName(fileName);
        fileInformation.setFileSize(fileSize);
        fileInformation.setAbstractFilePath(filePathInTorrent);

        return fileInformation;
    }

    @Test
    public void shouldLoadPiece() {
        doAnswer(invocation -> null).when(service).readPieceChunck(any(Path.class), anyLong(), anyInt(), any(ByteBuffer.class), any(byte[].class), anyInt());

        IFilesystemMetainfo metainfo = service.getFilesystemMetainfo();
        metainfo.setPieceLength(256);

        metainfo.addFileInformation(buildFileInformation("file1", 230, "file1"));
        metainfo.addFileInformation(buildFileInformation("file2", 230, "file2"));
        metainfo.addFileInformation(buildFileInformation("file3", 230, "file3"));
        metainfo.addFileInformation(buildFileInformation("file4", 230, "file4"));
        metainfo.addFileInformation(buildFileInformation("file5", 230, "file5"));

        service.loadPiece(0);

        verify(service).readPieceChunck(any(Path.class), eq(0L), eq(230), any(ByteBuffer.class), any(byte[].class), eq(0));
        verify(service).readPieceChunck(any(Path.class), eq(0L), eq(26), any(ByteBuffer.class), any(byte[].class), eq(230));

        service.loadPiece(1);
        verify(service).readPieceChunck(any(Path.class), eq(26L), eq(230), any(ByteBuffer.class), any(byte[].class), eq(0));
        verify(service).readPieceChunck(any(Path.class), eq(0L), eq(26), any(ByteBuffer.class), any(byte[].class), eq(230));

    }
}
