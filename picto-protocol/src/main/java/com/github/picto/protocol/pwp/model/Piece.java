package com.github.picto.protocol.pwp.model;

import com.github.picto.protocol.pwp.exception.BlockAlreadyDownloadedException;
import com.github.picto.protocol.pwp.exception.InvalidBlockSizeException;
import com.github.picto.protocol.pwp.exception.UnavailableBlockException;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Represents a piece of the torrent. Pieces are divided in an unknown number of blocks.
 * We will use the default 16KB size for now. TODO: make it configurable.
 *
 * This representation should be kept in memory for a very short amount of time and we should rely
 * on it only for sending pieces to someone or loading a piece. As soon as not needed, memory should be freed
 * and content flushed to file system.
 *
 * Created by Pierre on 15/09/15.
 */
public class Piece {
    // 2^14 bytes (16KB) is the mainline default size and will be the size we use.
    public static final int DEFAULT_SIZE = 16384;

    // 2^17 bytes (128KB) will be the maximum size before disconnect
    public static final int MAX_SIZE = 131072;

    private byte[] pieceContent;

    private BitSet haveBlock;

    private int blockCount;

    private final int pieceIndex;

    private Piece(final int pieceIndex) {
        this.pieceIndex = pieceIndex;
    }

    /**
     * Builds a full piece. All blocks are by default present.
     * @param pieceIndex
     * @param pieceContent
     */
    public Piece(int pieceIndex, final byte[] pieceContent) {
        this(pieceIndex);

        // TODO: do we need an array copy here to protect against outside modifications ?
        this.pieceContent = pieceContent;

        calculateBlockCount();
        haveBlock.set(0, blockCount - 1);
    }

    /**
     * Builds an empty piece. All block are by default not present.
     * @param pieceIndex
     * @param pieceSize
     */
    public Piece(int pieceIndex, final int pieceSize) {
        this(pieceIndex);

        pieceContent = new byte[pieceSize];
        Arrays.fill(pieceContent, (byte) 0);

        calculateBlockCount();
        haveBlock.clear();
    }

    private void calculateBlockCount() {
        // We can have one more block if the size isnt a multiple of DEFAULT_SIZE
        blockCount = pieceContent.length % DEFAULT_SIZE == 0 ? pieceContent.length / DEFAULT_SIZE : (pieceContent.length / DEFAULT_SIZE) + 1;

        haveBlock = new BitSet(blockCount);
    }

    /**
     * Insert a block into the piece. We assume the requested block size is the same as our internal one.
     * @param blockOffset
     * @param block
     * @throws InvalidBlockSizeException
     * @throws BlockAlreadyDownloadedException
     */
    public void insertBlock(final int blockOffset, byte[] block) throws InvalidBlockSizeException, BlockAlreadyDownloadedException {
        if (block.length > MAX_SIZE) {
            throw new InvalidBlockSizeException("The block size " + block.length + " is above the maximum authorized (" + MAX_SIZE + ")");
        }
        if (hasBlock(blockOffset, DEFAULT_SIZE)) {
            throw new BlockAlreadyDownloadedException("The provided block at offset " + blockOffset + " has already been downloaded for the current piece (" + pieceIndex + ")");
        }
    }

    /**
     * Indicates if a block is already present in the piece.
     * @param blockOffset
     * @param blockSize Since blocks can be of variable size we cannot rely on our own internal block limit
     * @return
     */
    public boolean hasBlock(final int blockOffset, final int blockSize) throws InvalidBlockSizeException {
        if (blockSize > MAX_SIZE) {
            throw new InvalidBlockSizeException("The block size " + blockSize + " is above the maximum authorized (" + MAX_SIZE + ")");
        }

        // We need to get the start index in our internal representation to start testing
        final int internalBlockOffset = blockOffset - (blockOffset % DEFAULT_SIZE);
        final int numberOfInternalBlocks = blockSize % DEFAULT_SIZE == 0 ? blockSize / DEFAULT_SIZE : blockSize / DEFAULT_SIZE + 1;

        for (int i = 0; i < numberOfInternalBlocks; i++) {
            if (!haveBlock.get(internalBlockOffset + i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if all blocks of the piece are set
     * @return
     */
    public boolean isPieceComplete() {
        return haveBlock.cardinality() == blockCount;
    }

    /**
     * Returns a block of the current piece.
     * @param blockOffset
     * @param size
     * @return
     */
    public byte[] getBlock(int blockOffset, int size) throws InvalidBlockSizeException, UnavailableBlockException {
        // If the size overflows we simply cut the the lowest available size. TODO: confirms this is correct behaviour.
        if (blockOffset + size > pieceContent.length) {
            size = (blockOffset + size) - pieceContent.length;
        }

        if (size > MAX_SIZE) {
            throw new InvalidBlockSizeException("We cannot cut the piece in blocks of size " + size);
        }

        if (!hasBlock(blockOffset, size)) {
            throw new UnavailableBlockException("The requested block at offset " + blockOffset + " is unavailable.");
        }

        return Arrays.copyOfRange(pieceContent, blockOffset, blockOffset + size);
    }

    public int getPieceIndex() {
        return pieceIndex;
    }
}
