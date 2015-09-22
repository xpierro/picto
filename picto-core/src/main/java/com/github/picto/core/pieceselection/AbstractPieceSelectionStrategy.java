package com.github.picto.core.pieceselection;

import java.util.BitSet;

/**
 * Created by Pierre on 23/09/15.
 */
public abstract class AbstractPieceSelectionStrategy implements IPieceSelectionStrategy {

    public int getNextNotDownloaded(BitSet downloadedPieces, int from) {
        if (from >= downloadedPieces.size()) {
            return -1;
        }
        return downloadedPieces.nextClearBit(from);
    }

}
