package com.github.picto.protocol.metainfo.model;

import com.github.picto.bencode.AbstractBEncodedDictionary;
import com.github.picto.bencode.annotation.BEncodeByteArray;
import com.github.picto.bencode.annotation.BEncodeDictionary;
import com.github.picto.bencode.annotation.BEncodeInteger;
import com.github.picto.bencode.annotation.BEncodeList;
import com.github.picto.bencode.type.BEncodeableDictionary;

import java.util.List;

/**
 * Implementation of the IMetaInfoInformation.
 * Created by Pierre on 25/08/15.
 */
@BEncodeDictionary(type = MetaInfoInformation.class)
public class MetaInfoInformation extends AbstractBEncodedDictionary implements IMetaInfoInformation {
    private static final int PIECE_HASH_SIZE = 20;

    protected int pieceLength;

    protected byte[] pieceHashes;

    protected boolean privateTracker;

    protected String rootName;

    protected boolean multifile;

    protected List<IMetaInfoFileDescription> files;

    public MetaInfoInformation() {

    }

    @BEncodeInteger(name = "piece length")
    @Override
    public int getPieceLength() {
        return pieceLength;
    }

    @Override
    public void setPieceLength(int pieceLength) {
        this.pieceLength = pieceLength;
    }

    @Override
    public byte[] getPieceHash(int pieceIndex) {
        int index = pieceIndex * PIECE_HASH_SIZE;
        byte[] hash = new byte[PIECE_HASH_SIZE];

        System.arraycopy(pieceHashes, index, hash, 0, PIECE_HASH_SIZE);

        return hash;
    }

    @BEncodeByteArray(name = "pieces")
    public byte[] getPieceHashes() {
        return pieceHashes;
    }

    public void setPieceHashes(byte[] pieceHashes) {
        this.pieceHashes = pieceHashes;
    }

    @Override
    public void setPieceHash(int pieceIndex, byte[] pieceHash) {
        int index = pieceIndex * PIECE_HASH_SIZE;

        System.arraycopy(pieceHash, 0, pieceHashes, index, PIECE_HASH_SIZE);
    }

    @Override
    public int getPieceCount() {
        return pieceHashes.length;
    }

    //TODO: unserializer has to understand is is also a getter
    @BEncodeInteger(name = "private")
    @Override
    public boolean isPrivate() {
        return privateTracker;
    }

    @Override
    public void setPrivate(boolean privateTracker) {
        this.privateTracker = privateTracker;
    }

    @BEncodeByteArray(name = "name")
    @Override
    public String getRootName() {
        return rootName;
    }

    @Override
    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    @Override
    public boolean isMultifiles() {
        return multifile;
    }

    @Override
    public void setMultiFile(boolean multiFile) {
        this.multifile = multiFile;
    }

    //TODO: list needs more parameters, like the class in case of dictionary conversion
    @BEncodeList(name = "files", elementType = BEncodeableDictionary.class)
    @Override
    public List<IMetaInfoFileDescription> getFiles() {
        return files;
    }

    @Override
    public void setFiles(List<IMetaInfoFileDescription> files) {
        this.files = files;
    }
}
