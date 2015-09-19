package com.github.picto.filesystem;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pierre on 20/09/15.
 */
public class FileSystemMetaTest {
    private FileInformation buildFileInformationWithSize(int size) {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setFileSize(size);
        return fileInformation;

    }

    private void assertPieceIndexes(FileInformation fileInformation, int expectedBeginIndex, int expectedEndIndex) {
        assertEquals(expectedBeginIndex, fileInformation.getStartPieceIndex());
        assertEquals(expectedEndIndex, fileInformation.getEndPieceIndex());
    }

    @Test
    public void addFileShouldCalculatePieceIndexes() {
        FilesystemMetainfo metainfo = new FilesystemMetainfo();
        metainfo.setPieceLength(256);

        FileInformation[] informations = new FileInformation[] {
                buildFileInformationWithSize(250),
                buildFileInformationWithSize(2),
                buildFileInformationWithSize(290),
                buildFileInformationWithSize(340),
                buildFileInformationWithSize(1),
                buildFileInformationWithSize(256),
                buildFileInformationWithSize(141),
                buildFileInformationWithSize(256),
                buildFileInformationWithSize(270),
        };

        for (FileInformation information : informations) {
            metainfo.addFileInformation(information);
        }

        // Now we assert the pieces indexes are correct
        assertPieceIndexes(informations[0], 0, 0);
        assertPieceIndexes(informations[1], 0, 0);
        assertPieceIndexes(informations[2], 0, 3);
        assertPieceIndexes(informations[3], 3, 4);
        assertPieceIndexes(informations[4], 4, 4);
        assertPieceIndexes(informations[5], 4, 5);
        assertPieceIndexes(informations[6], 5, 5);
        assertPieceIndexes(informations[7], 6, 6);
        assertPieceIndexes(informations[8], 7, 8);

    }
}
