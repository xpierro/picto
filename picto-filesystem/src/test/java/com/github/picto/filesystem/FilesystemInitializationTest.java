package com.github.picto.filesystem;

import com.github.picto.filesystem.event.FilesystemReadyEvent;
import com.github.picto.module.FilesystemModule;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * Tests the initialization of the filesystem
 *
 * Created by Pierre on 20/09/15.
 */
public class FilesystemInitializationTest {

    private FilesystemService service;

    private CountDownLatch countDownLatch;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new FilesystemModule());
        service = Mockito.spy(injector.getInstance(FilesystemService.class));
        service.subscribe(this);
    }

    @Subscribe
    public void initializationComplete(FilesystemReadyEvent event) {
        countDownLatch.countDown();
    }

    private FileInformation buildFileInformation(String fileName, long fileSize, String filePathInTorrent) {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setFileName(fileName);
        fileInformation.setFileSize(fileSize);
        fileInformation.setAbstractFilePath(filePathInTorrent);

        return fileInformation;
    }

    @Test
    public void shouldInitializeFilesystem() throws InterruptedException, IOException {
        doNothing().when(service).initializeFile(any(Path.class), anyLong());

        IFilesystemMetainfo metainfo = service.getFilesystemMetainfo();
        metainfo.setPieceLength(256);

        metainfo.addFileInformation(buildFileInformation("file1", 1L, "path/to/file1"));
        metainfo.addFileInformation(buildFileInformation("file1", 2L, "path/to/file2"));
        metainfo.addFileInformation(buildFileInformation("file1", 3L, "path/to/file3"));
        metainfo.addFileInformation(buildFileInformation("file1", 4L, "path/to/file4"));
        metainfo.addFileInformation(buildFileInformation("file1", 5L, "path/to/file5"));

        metainfo.setBasePath(Paths.get(this.getClass().getResource("").getPath()));

        countDownLatch = new CountDownLatch(1);
        service.initializeFilesystem();
        countDownLatch.await();

        verify(service).initializeFile(eq(Paths.get(this.getClass().getResource("").getPath()).resolve("path/to/file1")), eq(1L));
        verify(service).initializeFile(eq(Paths.get(this.getClass().getResource("").getPath()).resolve("path/to/file2")), eq(2L));
        verify(service).initializeFile(eq(Paths.get(this.getClass().getResource("").getPath()).resolve("path/to/file3")), eq(3L));
        verify(service).initializeFile(eq(Paths.get(this.getClass().getResource("").getPath()).resolve("path/to/file4")), eq(4L));
        verify(service).initializeFile(eq(Paths.get(this.getClass().getResource("").getPath()).resolve("path/to/file5")), eq(5L));
    }
}
